package com.example.tictactoe

import com.example.tictactoe.controller.GameController
import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.view.GameView
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

class TicTacToeApplication : Application() {
    override fun start(primaryStage: Stage) {
        val boardModel = BoardModel()
        val gameView = GameView()
        val gameController = GameController(boardModel, gameView)

        val scene = Scene(gameView.root, 300.0, 300.0)
        primaryStage.title = "Tic Tac Toe"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(TicTacToeApplication::class.java)
}