package com.example.tictactoe

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class TicTacToeApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(TicTacToeApplication::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(TicTacToeApplication::class.java)
}