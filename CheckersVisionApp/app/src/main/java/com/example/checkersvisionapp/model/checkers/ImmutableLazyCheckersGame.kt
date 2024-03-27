package com.example.checkersvisionapp.model.checkers

import android.content.Context
import android.os.Environment
import com.example.checkersvisionapp.persistence.StorageManager
import java.io.File
import java.util.Optional

class ImmutableLazyCheckersGame(override val name: String, override val numberOfPositions:Int, private val context: Context) : CheckersGame {
    private val gameDir = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        name
    )
    private val positions = mutableListOf<Optional<CheckersPosition>>()
    init {
        for (i in (0..<numberOfPositions))
            positions.add(Optional.empty())
    }

    override fun getPosition(index: Int): CheckersPosition {
        if (positions[index].isEmpty) {
            val img = StorageManager.loadPositionImg(context, gameDir, index)
            positions[index] = Optional.of(CheckersPosition(this, index, img))
        }
        return positions[index].get()
    }

    override fun forEachPosition(action: (index: Int, position: CheckersPosition) -> Unit) {
        positions.forEachIndexed { index, _ ->
            action(index, getPosition(index))
        }
    }

    override fun forEachPosition(action: (position: CheckersPosition) -> Unit) {
        positions.forEachIndexed { index, _ -> action(getPosition(index)) }
    }
}