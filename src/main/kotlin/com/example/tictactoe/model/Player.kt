package com.example.tictactoe.model

enum class Player(val symbol: String) {
    X("X"),
    O("O"),
    NONE(" ");

    fun opposite(): Player {
        return when (this) {
            X -> O
            O -> X
            NONE -> NONE
        }
    }
}