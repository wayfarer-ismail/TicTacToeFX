package com.example.tictactoe.bot

import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.model.Player
import com.example.tictactoe.view.GameView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import kotlin.math.abs
import kotlin.random.Random

class Bot(private val boardModel: BoardModel, private val gameView: GameView) {
    enum class Difficulty {
        EASY,
        MEDIUM,
        HARD,
        CHALLENGING
    }
    private var difficulty = Difficulty.EASY

    fun setDifficulty(difficulty: Difficulty) {
        this.difficulty = difficulty
    }

    fun makeMove() {
        println("making move")
        val emptyCells = mutableListOf<Pair<Int, Int>>()

        for (row in 0 until boardModel.boardSize) {
            for (col in 0 until boardModel.boardSize) {
                if (boardModel.getCell(row, col) == Player.NONE) {
                    emptyCells.add(row to col)
                }
            }
        }

        when (difficulty) {
            Difficulty.EASY -> easyMove(emptyCells)
            Difficulty.MEDIUM -> mediumMove(emptyCells)
            Difficulty.HARD -> hardMove(emptyCells)
            Difficulty.CHALLENGING -> challengingMove(emptyCells)
        }
    }

    private fun placeOnCell(row: Int, col: Int) {
        println("placing on cell $row, $col")
        gameView.getCell(row, col).fireEvent(
            MouseEvent(MouseEvent.MOUSE_CLICKED,
                0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 1, false,
                false, false, false, true, false,
                false, true, false, true, null)
        )
    }

    private fun easyMove(emptyCells: MutableList<Pair<Int, Int>>) {
        if (emptyCells.isNotEmpty() && boardModel.checkWin() == Player.NONE) {
            val randomIndex = Random.nextInt(emptyCells.size)
            val (row, col) = emptyCells[randomIndex]
            placeOnCell(row, col)
        }
    }

    private fun mediumMove(emptyCells: MutableList<Pair<Int, Int>>) {

        for (row in 0 until boardModel.boardSize) {
            for (col in 0 until boardModel.boardSize) {
                if (boardModel.getCell(row, col) == Player.NONE) {
                    emptyCells.add(row to col)
                }
            }
        }

        // Check for potential winning move for the bot
        for ((row, col) in emptyCells) {
            boardModel.setCell(row, col, Player.O)
            if (boardModel.checkWin() == Player.O) {
                boardModel.setCell(row, col, Player.NONE)
                placeOnCell(row, col)
                return
            }
            boardModel.setCell(row, col, Player.NONE) // Reset the cell
        }

        // Check for potential winning move for the user and block it
        for ((row, col) in emptyCells) {
            boardModel.setCell(row, col, Player.X)
            if (boardModel.checkWin() == Player.X) {
                boardModel.setCell(row, col, Player.NONE)
                placeOnCell(row, col)
                return
            }
            boardModel.setCell(row, col, Player.NONE) // Reset the cell
        }

        // If no easy win or block is available, make a random move
        easyMove(emptyCells)
    }

    private fun hardMove(emptyCells: List<Pair<Int, Int>>) {
        val bestMove = minimax(boardModel, Player.O, emptyCells, 0)
        println("best move: ${bestMove.second} with score ${bestMove.first}")
        val (row, col) = bestMove.second
        if (row == -1 || col == -1)
            easyMove(emptyCells.toMutableList())
        else
            placeOnCell(row, col)
    }

    private fun minimax(board: BoardModel,
                        player: Player,
                        emptyCells: List<Pair<Int, Int>>,
                        depth: Int): Pair<Int, Pair<Int, Int>> {

        if (board.checkWin() == Player.O) {
            return 10 - depth to Pair(-1, -1) // Maximize
        } else if (board.checkWin() == Player.X) {
            return -10 + depth to Pair(-1, -1) // Minimize
        } else if (board.isBoardFull()) {
            return 0 to Pair(-1, -1) // Tie
        }

        val moves = mutableListOf<Pair<Int, Pair<Int, Int>>>()

        for (cell in emptyCells) {
            val newBoard = board.clone()
            newBoard.setCell(cell.first, cell.second, player)

            // Recursively call minimax on the new board
            val (score, _) = minimax(newBoard, player.opposite(), emptyCells.minus(cell), depth + 1)
            moves.add(score to cell)
        }

        val isLoosing = depth == 0 && moves.fold(true) { acc, pair -> acc && pair.first <= 0 }
        // if the users next move is a win for them, choose the move that blocks their win,
        // otherwise weigh between potential wins and losses for the bot using abs value.
        return if (isLoosing) moves.maxBy { it.first } else moves.maxBy { abs(it.first) }
    }

    private fun challengingMove(emptyCells: List<Pair<Int, Int>>) {
        val player = Player.O // The bot is Player O

        // Rule 1: Win if possible
        val winMove = findWinningMove(boardModel, player, emptyCells)
        if (winMove != null) {
            placeOnCell(winMove.first, winMove.second)
            return
        }

        // Rule 2: Block opponent's win if they have two in a row
        val blockMove = findWinningMove(boardModel, player.opposite(), emptyCells)
        if (blockMove != null) {
            placeOnCell(blockMove.first, blockMove.second)
            return
        }

        // Rule 3: Create a fork if possible
        val forkMove = findForkMove(boardModel, player, emptyCells)
        if (forkMove != null) {
            placeOnCell(forkMove.first, forkMove.second)
            return
        }

        // Rule 4: Block opponent's fork if they have a fork opportunity
        val blockForkMove = blockOpponentFork(boardModel, player, emptyCells)
        if (blockForkMove != null) {
            placeOnCell(blockForkMove.first, blockForkMove.second)
            return
        }

        // Rule 5: Play the center if available
        if (emptyCells.contains(Pair(1, 1))) {
            placeOnCell(1, 1)
            return
        }

        // Rule 6: Play the opposite corner if opponent is in a corner
        val oppositeCornerMove = playOppositeCorner(boardModel, player, emptyCells)
        if (oppositeCornerMove != null) {
            placeOnCell(oppositeCornerMove.first, oppositeCornerMove.second)
            return
        }

        // Rule 7: Play an empty corner
        val emptyCornerMove = playEmptyCorner(emptyCells)
        if (emptyCornerMove != null) {
            placeOnCell(emptyCornerMove.first, emptyCornerMove.second)
            return
        }

        // Rule 8: Play an empty side
        val emptySideMove = playEmptySide(emptyCells)
        if (emptySideMove != null) {
            placeOnCell(emptySideMove.first, emptySideMove.second)
            return
        }
    }

    // Helper function to find a winning move for a player
    private fun findWinningMove(board: BoardModel, player: Player, emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        for ((row, col) in emptyCells) {
            val newBoard = board.clone()
            newBoard.setCell(row, col, player)
            if (newBoard.checkWin() == player) {
                return Pair(row, col)
            }
        }
        return null
    }

    // Helper function to find a fork move for a player
    private fun findForkMove(board: BoardModel, player: Player, emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        for ((row, col) in emptyCells) {
            val newBoard = board.clone()
            newBoard.setCell(row, col, player)
            if (createsFork(newBoard, player, emptyCells)) {
                return Pair(row, col)
            }
        }
        return null
    }

    // Helper function to check if a move creates a fork for a player
    private fun createsFork(board: BoardModel, player: Player, emptyCells: List<Pair<Int, Int>>): Boolean {
        val forkCount = emptyCells.count { (row, col) ->
            val newBoard = board.clone()
            newBoard.setCell(row, col, player)
            hasTwoWaysToWin(newBoard, player, emptyCells)
        }
        return forkCount > 1
    }

    // Helper function to check if a player has two ways to win on the board
    private fun hasTwoWaysToWin(board: BoardModel, player: Player, emptyCells: List<Pair<Int, Int>>): Boolean {
        val winningMoves = emptyCells.filter { (row, col) ->
            val newBoard = board.clone()
            newBoard.setCell(row, col, player)
            newBoard.checkWin() == player
        }

        return winningMoves.size >= 2
    }


    // Helper function to block an opponent's fork
    private fun blockOpponentFork(board: BoardModel, player: Player, emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        for ((row, col) in emptyCells) {
            val newBoard = board.clone()
            newBoard.setCell(row, col, player.opposite())
            if (createsFork(newBoard, player.opposite(), emptyCells)) {
                return Pair(row, col)
            }
        }
        return null
    }

    // Helper function to play the opposite corner if opponent is in a corner
    private fun playOppositeCorner(board: BoardModel, player: Player, emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        val cornerOppositePairs = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        for ((row, col) in cornerOppositePairs) {
            if (board.getCell(row, col) == player.opposite() && emptyCells.contains(Pair(2 - row, 2 - col))) {
                return Pair(2 - row, 2 - col)
            }
        }
        return null
    }

    // Helper function to play an empty corner
    private fun playEmptyCorner(emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        val emptyCornerMoves = emptyCells.filter { (row, col) ->
            listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2)).contains(Pair(row, col))
        }
        return if (emptyCornerMoves.isNotEmpty()) emptyCornerMoves.random() else null
    }

    // Helper function to play an empty side
    private fun playEmptySide(emptyCells: List<Pair<Int, Int>>): Pair<Int, Int>? {
        val emptySideMoves = emptyCells.filter { (row, col) ->
            listOf(Pair(0, 1), Pair(1, 0), Pair(1, 2), Pair(2, 1)).contains(Pair(row, col))
        }
        return if (emptySideMoves.isNotEmpty()) emptySideMoves.random() else null
    }

}
