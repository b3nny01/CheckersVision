package com.example.checkersvisionapp.model.predicted

import android.util.Log
import com.example.checkersvisionapp.model.checkers.CheckersGame
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import org.pytorch.Module
import java.lang.StackWalker.Option
import java.util.Optional

class LazyPredictedGame(private val checkersGame: CheckersGame, module: Module) : PredictedGame {
    private val classifier = Predictor(module)
    private val positions = mutableListOf<Optional<PredictedPosition>>()

    init {
        for (i in (0..<checkersGame.numberOfPositions)){
            positions.add(Optional.empty())
        }
    }

    private fun classifyPosition(index: Int) {
        Log.w("predicting","predicting position $index")
        positions[index] = Optional.of(
            PredictedPosition(
                this,
                index,
                classifier.predictedSquaresOf(checkersGame.getPosition(index))
            )
        )
    }

    override fun getPosition(index: Int): PredictedPosition {
        if (positions[index].isEmpty) {
            classifyPosition(index)
        }
        return positions[index].get()
    }

    override fun forEachPosition(action: (index: Int, position: PredictedPosition) -> Unit) {
        positions.forEachIndexed { index, _ ->
            action(index, getPosition(index))
        }
    }

    override fun forEachPosition(action: (position: PredictedPosition) -> Unit) {
        positions.forEachIndexed { index, _ ->
            action(getPosition(index))
        }
    }
}