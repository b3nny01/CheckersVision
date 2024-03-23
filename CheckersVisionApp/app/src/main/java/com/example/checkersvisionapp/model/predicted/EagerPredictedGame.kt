package com.example.checkersvisionapp.model.predicted

import com.example.checkersvisionapp.model.checkers.CheckersGame
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import org.pytorch.Module

class EagerPredictedGame  constructor(private val checkersGame: CheckersGame, module:Module):
    PredictedGame {
    private val positions = mutableListOf<PredictedPosition>()
    init {
        val classifier= Predictor(module)
        checkersGame.forEachPosition { index, position ->
            this.addPosition(index, classifier.predictedSquaresOf(position))
        }
    }
    
    private fun addPosition(squares:MutableList<PredictedSquare>): Boolean {
        return positions.add(PredictedPosition(this,positions.size,squares))
    }

    private fun addPosition(index: Int, squares: MutableList<PredictedSquare>) {
        return positions.add(index, PredictedPosition(this,index, squares))
    }

    override fun getPosition(index: Int): PredictedPosition {
        return positions[index]
    }

    override fun forEachPosition(action:(index:Int, position: PredictedPosition)->Unit){
        positions.forEachIndexed(action)
    }
    override fun forEachPosition(action:(position: PredictedPosition)->Unit){
        positions.forEach(action)
    }
}