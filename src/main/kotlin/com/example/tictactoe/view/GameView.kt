package com.example.tictactoe.view

import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

class GameView {
    val root: GridPane = GridPane()

    init {
        root.alignment = Pos.CENTER
        root.hgap = 10.0
        root.vgap = 10.0

        val boardSize = 3

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cell = createCell()
                GridPane.setRowIndex(cell, row)
                GridPane.setColumnIndex(cell, col)
                root.children.add(cell)
            }
        }
    }

    private fun createCell(): StackPane {
        val cellSize = 100.0 // Adjust this for cell size

        val cellBackground = Rectangle(cellSize, cellSize)
        cellBackground.fill = Color.WHITE
        cellBackground.stroke = Color.BLACK

        val text = Text()
        text.font = Font.font(36.0)
        text.fill = Color.BLACK

        val cell = StackPane()
        cell.prefWidth = cellSize
        cell.prefHeight = cellSize
        cell.children.addAll(cellBackground, text)

        // Add event handling for user interaction (e.g., mouse click) here

        return cell
    }

    // Add methods to update the UI based on the board state here
}
