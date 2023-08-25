package com.example.tictactoe.view

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
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
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.util.Duration

class GameView {
    val root: StackPane = StackPane()
    val boardPane: GridPane = GridPane()

    init {
        root.alignment = Pos.CENTER
        root.minWidth = 500.0
        root.minHeight = 500.0

        boardPane.alignment = Pos.CENTER

        val backgroundImage = Image("https://i.ibb.co/VH8970J/tictactoe-background-1.png")

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
        label.font = Font.font("Arial", FontWeight.MEDIUM, 50.0)

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
        difficultyComboBox.items.addAll("Easy", "Medium", "Hard", "Challenging")
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
        val normalStyle = "-fx-font-family: Arial; -fx-font-size: 18px; -fx-background-color: white; -fx-border-color: orange; -fx-border-width: 2px; -fx-font-weight: normal;"
        node.style = normalStyle
        node.setOnMouseEntered {
            node.style = "$normalStyle -fx-effect: dropshadow(gaussian, orange, 10, 0.5, 0, 0);"
        }
        node.setOnMouseExited {
            node.style = normalStyle
        }
        node.prefWidth = 160.0
    }

    fun switchToBoard() {
        root.children.clear()
        root.children.add(boardPane)
    }

    fun createFarewellScreen(message: String) {
        val farewellText = Text(message)
        farewellText.fill = Color.WHITE
        farewellText.font = Font.font("Arial", FontWeight.BOLD, 30.0)

        // Create a colored rectangle as the background
        val background = Rectangle(250.0, 80.0) // Adjust width and height as needed
        background.fill = Color.DARKORANGE.deriveColor(0.4, 1.0, 1.0, 0.6)

        // Create a container to hold the background and the text
        val farewellPane = StackPane()
        farewellPane.alignment = Pos.CENTER
        farewellPane.children.addAll(background, farewellText)

        root.children.clear()
        root.children.add(farewellPane)
        playWinAnimation(root)
    }

    private fun playWinAnimation(root: StackPane) {
        val pane = StackPane()
        pane.style = "-fx-background-color: #0ff7f0; -fx-padding: 20px;"
        pane.opacity = 0.0 // Start with zero opacity

        val timeline = Timeline()
        val fadeIn = KeyFrame(
            Duration.seconds(2.0),
            KeyValue(pane.opacityProperty(), 0.25)
        )
        val fadeOut = KeyFrame(
            Duration.seconds(3.0),
            KeyValue(pane.opacityProperty(), 0.0)
        )
        timeline.keyFrames.addAll(fadeIn, fadeOut)

        root.children.add(pane)

        timeline.setOnFinished {
            // Remove the pane node when the animation is finished
            root.children.remove(pane)
        }

        timeline.play()
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
