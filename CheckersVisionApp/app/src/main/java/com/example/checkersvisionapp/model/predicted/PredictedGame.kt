package com.example.checkersvisionapp.model.predicted

import com.example.checkersvisionapp.model.checkers.CheckersGame
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import org.pytorch.Module

interface PredictedGame {
    companion object {
        fun of(checkersGame: CheckersGame, module: Module): PredictedGame {
            return LazyPredictedGame(checkersGame,module)
        }
    }

    fun getPosition(index: Int): PredictedPosition
    fun forEachPosition(action: (index: Int, position: PredictedPosition) -> Unit)
    fun forEachPosition(action: (position: PredictedPosition) -> Unit)
}