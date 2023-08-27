package com.example.tictactoe.model

data class Scores(private var playerXScore: Int = 0, private var playerOScore: Int = 0) {
    val playerXWins: Int
        get() = playerXScore

    val playerOWins: Int
        get() = playerOScore

    fun incrementPlayerXScore() = playerXScore++

    fun incrementPlayerOScore() = playerOScore++

    fun resetScores() {
        playerXScore = 0
        playerOScore = 0
    }
}