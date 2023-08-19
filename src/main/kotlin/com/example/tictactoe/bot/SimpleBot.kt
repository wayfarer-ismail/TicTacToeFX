package com.example.tictactoe.bot

import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.model.Player
import com.example.tictactoe.view.GameView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import kotlin.random.Random

class SimpleBot(private val boardModel: BoardModel, private val gameView: GameView) {
    private val MAXDEPTH = 8

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

        hardMove(emptyCells)
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

        if (board.checkWin() == player) {
            return 100 to Pair(-1, -1) // Maximize
        } else if (board.checkWin() == player.opposite()) {
            return -100 to Pair(-1, -1) // Tie
        } else if (emptyCells.isEmpty() || depth == MAXDEPTH) {
            return 0 to Pair(-1, -1) // Minimize
        }

        var bestScore = if (player == Player.O) Int.MIN_VALUE else Int.MAX_VALUE
        var bestMove = Pair(-1, -1)

        for (cell in emptyCells) {
            val newBoard = board.clone()
            newBoard.setCell(cell.first, cell.second, player)

            // Recursively call minimax on the new board
            val (score, _) = minimax(newBoard, if (player == Player.O) Player.X else Player.O, emptyCells.minus(cell), depth + 1)

            // Update the best score and move if necessary
            if (player == Player.O && score > bestScore || player == Player.X && score < bestScore) {
                bestScore = score
                bestMove = cell
            }
        }

        return bestScore to bestMove
    }

}
