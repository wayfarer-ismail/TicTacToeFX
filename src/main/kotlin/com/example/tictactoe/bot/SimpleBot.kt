package com.example.tictactoe.bot

import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.model.Player
import com.example.tictactoe.view.GameView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import kotlin.random.Random

class SimpleBot(private val boardModel: BoardModel, private val gameView: GameView) {

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

        mediumMove(emptyCells)
    }

    private fun placeOnCell(row: Int, col: Int) {
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
            val originalSymbol = boardModel.getCell(row, col)
            boardModel.setCell(row, col, Player.O)
            if (boardModel.checkWin() == Player.O) {
                boardModel.setCell(row, col, Player.NONE)
                placeOnCell(row, col)
                return
            }
            boardModel.setCell(row, col, originalSymbol) // Reset the cell
        }

        // Check for potential winning move for the user and block it
        for ((row, col) in emptyCells) {
            //val originalSymbol = boardModel.getCell(row, col)
            boardModel.setCell(row, col, Player.X)
            //println("Checking for x win at $row, $col")
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
}
