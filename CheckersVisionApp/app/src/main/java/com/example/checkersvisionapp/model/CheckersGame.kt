package com.example.checkersvisionapp.model

import android.graphics.Bitmap

class CheckersGame(val name:String){
    private val positions = mutableListOf<CheckersPosition>()
    val numberOfPositions:Int
        get() = positions.size
    fun addPosition(bitmap: Bitmap):Boolean{
        return positions.add(CheckersPosition(this,positions.size,bitmap))
    }

    fun addPosition(index:Int,bitmap: Bitmap)
    {
        return positions.add(index,CheckersPosition(this,index,bitmap))
    }

    fun getPosition(index:Int):CheckersPosition
    {
        return positions[index]
    }

    fun forEachPosition(action:(index:Int,position:CheckersPosition)->Unit){
        positions.forEachIndexed(action)
    }
    fun forEachPosition(action:(position:CheckersPosition)->Unit){
        positions.forEach(action)
    }
}