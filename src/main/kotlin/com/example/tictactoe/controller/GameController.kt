package com.example.tictactoe.controller

import com.example.tictactoe.bot.Bot
import com.example.tictactoe.bot.Bot.Difficulty
import com.example.tictactoe.model.BoardModel
import com.example.tictactoe.model.Player
import com.example.tictactoe.model.Scores
import com.example.tictactoe.view.GameView
import com.google.gson.Gson
import java.io.File

class GameController(private val boardModel: BoardModel, private val gameView: GameView) {

    private val fs = File.separator
    private val scoresFilePath = File(".").canonicalPath +
            "${fs}src${fs}main${fs}kotlin${fs}com${fs}example${fs}tictactoe${fs}model${fs}scores.json"

    // Create an instance of Scores
    private val scores = loadScores()
    private val bot: Bot = Bot(boardModel, gameView)

    init {
        // Add a shutdown hook to save scores on exit
        Runtime.getRuntime().addShutdownHook(Thread {
            // Save scores when the application exits
            saveScores(scores)
        })

        boardModel.bot = bot
        gameView.createWelcomeScreen(::handleStartGame, ::handleDifficultySelection, scores)
    }

    private fun saveScores(scores: Scores) {
        val gson = Gson()
        val json = gson.toJson(scores)
        val file = File(scoresFilePath)
        file.createNewFile()
        file.writeText(json)
    }

    private fun loadScores(): Scores {
        val gson = Gson()
        val scoresFile = File(scoresFilePath)

        return if (scoresFile.exists()) {
            val json = scoresFile.readText()
            gson.fromJson(json, Scores::class.java) // Load scores from file
        } else {
            // Initialize with default values
            val defaultScores = Scores(0, 0)
            saveScores(defaultScores)

            defaultScores   // Return default scores
        }
    }

    private fun handleStartGame() {
        gameView.switchToBoard() // Switch to the game board
        initializeCells() // Initialize cells now that the board is displayed
    }

    private fun handleDifficultySelection(selectedDifficulty: String) {
        when (selectedDifficulty) {
            "Easy" -> bot.setDifficulty(Difficulty.EASY)
            "Medium" -> bot.setDifficulty(Difficulty.MEDIUM)
            "Hard" -> bot.setDifficulty(Difficulty.HARD)
            "Challenging" -> bot.setDifficulty(Difficulty.CHALLENGING)
            else -> throw IllegalArgumentException("Invalid difficulty")
        }
    }

    private fun initializeCells() {
        for (row in 0 until boardModel.boardSize) {
            for (col in 0 until boardModel.boardSize) {
                val cell = gameView.createCell()
                cell.setOnMouseClicked { handleCellClick(row, col) }
                gameView.boardPane.add(cell, col, row)
            }
        }
    }

    private fun handleCellClick(row: Int, col: Int) {
        if (boardModel.makeMove(row, col)) {
            gameView.updateUI(row, col, boardModel.getCell(row, col).symbol)
        }
        val winner = boardModel.checkWin()
        if (winner != Player.NONE || boardModel.isBoardFull()) {
            handleGameOver(winner)
        }
    }

    private fun handleGameOver(winner: Player) {
        // Disable further moves and show win message
        disableCells()

        val message = if (winner == Player.NONE) {
            "It's a draw!"
        } else {
            if (winner == Player.X) scores.incrementPlayerXScore() else scores.incrementPlayerOScore()
            "Player ${winner.symbol} wins!"
        }

        gameView.createFarewellScreen(message, ::resetGame)
    }

    private fun resetGame() {
        // Reset the board model
        boardModel.resetBoard()

        // Clear the board view and enable cells
        gameView.resetBoard()
        gameView.createWelcomeScreen(::handleStartGame, ::handleDifficultySelection, scores)
    }

    private fun disableCells() {
        for (row in 0 until boardModel.boardSize) {
            for (col in 0 until boardModel.boardSize) {
                gameView.getCell(row, col).isDisable = true
            }
        }
    }

}
