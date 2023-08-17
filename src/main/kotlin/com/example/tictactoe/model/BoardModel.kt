package com.example.tictactoe.model

import com.example.tictactoe.bot.SimpleBot

class BoardModel(val boardSize: Int = 3) {
    private val board = Array(boardSize) { Array(boardSize) { Player.NONE } }
    private var currentPlayer = Player.X
    var bot: SimpleBot? = null

    fun makeMove(row: Int, col: Int): Boolean {
        if (row in 0 until boardSize && col in 0 until boardSize && board[row][col] == Player.NONE) {
            board[row][col] = currentPlayer

            // After the user's move, if the game is not over, and it's the bots turn, let the bot make a move
            if (currentPlayer == Player.X) {
                currentPlayer = Player.O
                bot?.makeMove()
            } else {
                currentPlayer = Player.X
            }
            return true
        }
        return false
    }

    fun getCell(row: Int, col: Int): Player {
        return if (row in 0 until boardSize && col in 0 until boardSize) board[row][col] else Player.NONE
    }

    fun setCell(row: Int, col: Int, player: Player) {
        if (row in 0 until boardSize && col in 0 until boardSize) {
            board[row][col] = player
        }
    }


    fun checkWin(): Player {
        // Check rows
        for (row in 0 until boardSize) {
            if (board[row][0] != Player.NONE && board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                return board[row][0]
            }
        }

        // Check columns
        for (col in 0 until boardSize) {
            if (board[0][col] != Player.NONE && board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                return board[0][col]
            }
        }

        // Check diagonals
        if (board[0][0] != Player.NONE && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0]
        }
        if (board[0][2] != Player.NONE && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2]
        }

        return Player.NONE
    }

    fun isBoardFull(): Boolean {
        return board.all { row -> row.all { cell -> cell != Player.NONE } }
    }

    fun resetBoard() {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                board[row][col] = Player.NONE
            }
        }
        currentPlayer = Player.X
    }
}
