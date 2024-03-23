package com.example.checkersvisionapp.model.checkers

import android.content.Context

interface CheckersGame {

    val name:String
    val numberOfPositions:Int
    fun getPosition(index:Int): CheckersPosition
    fun forEachPosition(action:(index:Int,position: CheckersPosition)->Unit)
    fun forEachPosition(action:(position: CheckersPosition)->Unit)

}