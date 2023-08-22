package com.example.tictactoe.bot

import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.model.Player
import com.example.tictactoe.view.GameView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import kotlin.math.abs
import kotlin.random.Random

class SimpleBot(private val boardModel: BoardModel, private val gameView: GameView) {
    enum class Difficulty {
        EASY,
        MEDIUM,
        HARD
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
}
