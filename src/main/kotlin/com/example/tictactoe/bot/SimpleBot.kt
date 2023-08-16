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

        if (emptyCells.isNotEmpty() && boardModel.checkWin() == Player.NONE) {
            val randomIndex = Random.nextInt(emptyCells.size)
            val (row, col) = emptyCells[randomIndex]
            gameView.getCell(row, col).fireEvent(
                MouseEvent(MouseEvent.MOUSE_CLICKED,
                0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 1, false,
                    false, false, false, true, false,
                    false, true, false, true, null)
            )
        }
    }
}
