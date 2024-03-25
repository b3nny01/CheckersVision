package com.example.checkersvisionapp.model.checkers

import android.graphics.Bitmap

class ImmutableEagerCheckersGame(override val name:String, positionImgs: List<Bitmap> = listOf()):CheckersGame{
    private val positions= positionImgs.mapIndexed { index, bitmap ->  CheckersPosition(this,index,bitmap) }.toList()
    override val numberOfPositions:Int
        get() = positions.size

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