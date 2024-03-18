package com.example.checkersvisionapp.controller.oldGames

import com.example.checkersvisionapp.model.CheckersGame

class CheckersGameView(val id:Long,private val game: CheckersGame) {
    val name=game.name
}