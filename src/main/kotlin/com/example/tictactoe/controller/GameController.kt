package com.example.tictactoe.controller

import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.model.Player
import com.example.tictactoe.view.GameView
import javafx.scene.Node
import javafx.scene.control.Label

import javafx.scene.input.MouseButton

class GameController(private val boardModel: BoardModel, private val gameView: GameView) {
    init {
        initializeCells()
    }

    private fun initializeCells() {
        for (row in 0 until boardModel.boardSize) {
            for (col in 0 until boardModel.boardSize) {
                val cell = gameView.getCell(row, col)
                cell.setOnMouseClicked { event ->
                    if (event.button == MouseButton.PRIMARY) {
                        handleCellClick(row, col)
                    }
                }
            }
        }
    }

    private fun handleCellClick(row: Int, col: Int) {
        if (boardModel.makeMove(row, col)) {
            updateUI(row, col)
            val winner = boardModel.checkWin()
            if (winner != Player.NONE) {
                // Handle game over (e.g., show winner, disable cells)
            } else if (boardModel.isBoardFull()) {
                // Handle draw (e.g., show draw message, disable cells)
            }
        }
    }

    private fun updateUI(row: Int, col: Int) {
        val cell = gameView.getCell(row, col)
        val currentPlayer = boardModel.getCell(row, col)
        cell.children.stream()
            .filter { node: Node? -> node is Label }
            .map { node: Node -> node as Label }
            .findFirst()
            .ifPresent { label: Label ->
                label.text = currentPlayer.symbol
            }
    }
}
