package com.example.checkersvisionapp.model.checkers

import android.graphics.Bitmap

class MutableCheckersGame(override val name:String):CheckersGame{
    private val positions = mutableListOf<CheckersPosition>()
    override val numberOfPositions:Int
        get() = positions.size
    fun addPosition(bitmap: Bitmap):Boolean{
        return positions.add(CheckersPosition(this,positions.size,bitmap))
    }

    fun addPosition(index:Int,bitmap: Bitmap)
    {
        return positions.add(index, CheckersPosition(this,index,bitmap))
    }

    override fun getPosition(index:Int): CheckersPosition
    {
        return positions[index]
    }

    override fun forEachPosition(action:(index:Int, position: CheckersPosition)->Unit){
        positions.forEachIndexed(action)
    }
    override fun forEachPosition(action:(position: CheckersPosition)->Unit){
        positions.forEach(action)
    }
}