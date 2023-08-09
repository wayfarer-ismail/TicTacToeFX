package com.example.tictactoe.model

class BoardModel(private val boardSize: Int = 3) {
    private val board = Array(boardSize) { Array(boardSize) { Player.NONE } }
    private var currentPlayer = Player.X

    fun makeMove(row: Int, col: Int): Boolean {
        if (row in 0 until boardSize && col in 0 until boardSize && board[row][col] == Player.NONE) {
            board[row][col] = currentPlayer
            currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
            return true
        }
        return false
    }

    fun getCell(row: Int, col: Int): Player {
        return if (row in 0 until boardSize && col in 0 until boardSize) board[row][col] else Player.NONE
    }

    fun checkWin(): Player {
        // Implement win checking logic here
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
