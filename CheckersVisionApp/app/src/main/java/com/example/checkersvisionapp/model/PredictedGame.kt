package com.example.checkersvisionapp.model

import org.pytorch.Module

class PredictedGame private constructor(private val checkersGame: CheckersGame) {
    companion object{
        fun of(checkersGame: CheckersGame,module:Module):PredictedGame{
            val classifier= Predictor(module)
            val predictedGame=PredictedGame(checkersGame)
            checkersGame.forEachPosition { index, position ->
                predictedGame.addPosition(index, classifier.predictedSquaresOf(position))
            }
            return predictedGame
        }
    }

    private val positions = mutableListOf<PredictedPosition>()

    
    private fun addPosition(squares:MutableList<PredictedSquare>): Boolean {
        return positions.add(PredictedPosition(this,positions.size,squares))
    }

    private fun addPosition(index: Int, squares: MutableList<PredictedSquare>) {
        return positions.add(index,PredictedPosition(this,index, squares))
    }

    fun getPosition(index: Int):PredictedPosition{
        return positions[index]
    }

    fun forEachPosition(action:(index:Int,position:PredictedPosition)->Unit){
        positions.forEachIndexed(action)
    }
    fun forEachPosition(action:(position:PredictedPosition)->Unit){
        positions.forEach(action)
    }
}