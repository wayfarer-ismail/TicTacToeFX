package com.example.tictactoe.view

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.effect.BoxBlur
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

class GameView {
    val root: StackPane = StackPane()
    val boardPane: GridPane = GridPane()

    init {
        root.alignment = Pos.CENTER
        boardPane.alignment = Pos.CENTER

        val backgroundImage =
            Image("https://wallpapercave.com/wp/wp9844348.png")

        val background = Background(
            BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                BackgroundSize(
                    BackgroundSize.AUTO,
                    BackgroundSize.AUTO,
                    false,
                    false,
                    true,
                    true
                )
            )
        )

        root.background = background
    }

    fun createCell(): StackPane {
        val cellSize = 100.0 // Adjust this for cell size

        val cellBackground = Rectangle(cellSize, cellSize)
        cellBackground.fill = Color.WHITE
        cellBackground.stroke = Color.DARKORANGE

        val label = Label("-")
        label.font = Font.font(36.0)

        val cell = StackPane()
        cell.prefWidth = cellSize
        cell.prefHeight = cellSize
        cell.children.addAll(cellBackground, label)

        // Add event handling for hover and click effects
        // Add shadow on hover
        cell.setOnMouseEntered { cellBackground.effect = BoxBlur() }
        // Remove shadow on mouse exit
        cell.setOnMouseExited { cellBackground.effect = null }
        // Make the cell color slightly dimmer on click
        cell.setOnMousePressed { cellBackground.fill = Color.LIGHTGRAY }
        // Restore the original color when the mouse is released
        cell.setOnMouseReleased { cellBackground.fill = Color.WHITE }

        return cell
    }

    fun getCell(row: Int, col: Int): StackPane {
        return boardPane.children[row * 3 + col] as StackPane
    }

    fun createWelcomeScreen(startGameHandler: () -> Unit, difficultySelectionHandler: (String) -> Unit) {
        val welcomePane = VBox(20.0) // Create a VBox with spacing

        val startButton = Button("Start Game")
        applyTheme(startButton)
        startButton.setOnAction {
            startGameHandler.invoke()
        }

        val difficultyComboBox = ComboBox<String>()
        difficultyComboBox.items.addAll("Easy", "Medium", "Hard")
        difficultyComboBox.selectionModel.selectFirst()
        applyTheme(difficultyComboBox)

        val selectButton = Button("Select Difficulty")
        applyTheme(selectButton)
        selectButton.setOnAction {
            val selectedDifficulty = difficultyComboBox.selectionModel.selectedItem
            difficultySelectionHandler.invoke(selectedDifficulty)
        }

        welcomePane.alignment = Pos.CENTER // Align elements in the center
        welcomePane.children.addAll(difficultyComboBox, selectButton, startButton)

        root.children.add(welcomePane)
    }

    private fun applyTheme(node: Control) {
        node.style = "-fx-font-size: 18px; -fx-background-color: white; -fx-border-color: orange; -fx-border-width: 2px;"
        node.setOnMouseEntered {
            node.style = "-fx-font-size: 18px; -fx-background-color: white; -fx-border-color: orange; -fx-border-width: 2px; -fx-effect: dropshadow(gaussian, orange, 10, 0.5, 0, 0);"
        }
        node.setOnMouseExited {
            node.style = "-fx-font-size: 18px; -fx-background-color: white; -fx-border-color: orange; -fx-border-width: 2px;"
        }
        node.prefWidth = 160.0
    }

    fun switchToBoard() {
        root.children.clear()
        root.children.add(boardPane)
    }

    fun createFarewellScreen(message: String) {
        val farewellText = Text(message)
        farewellText.font = Font.font(24.0)

        root.children.clear()
        root.children.add(farewellText)
    }

    fun updateUI(row: Int, col: Int, currentPlayer: String) {
        val cell = getCell(row, col)
        cell.children.stream()
            .filter { node: Node? -> node is Label }
            .map { node: Node -> node as Label }
            .findFirst()
            .ifPresent { label: Label ->
                label.text = currentPlayer
            }
    }
}
