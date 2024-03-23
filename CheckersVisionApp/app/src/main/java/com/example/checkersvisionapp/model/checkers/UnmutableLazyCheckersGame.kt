package com.example.checkersvisionapp.model.checkers

import android.content.Context
import android.os.Environment
import com.example.checkersvisionapp.persistence.StorageManager
import java.io.File
import java.util.Optional

class UnmutableLazyCheckersGame(override val name: String, private val context: Context) : CheckersGame {
    private val gameDir = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        name
    )
    private val positions = mutableListOf<Optional<CheckersPosition>>()


    override val numberOfPositions: Int
        get() = positions.size

    override fun getPosition(index: Int): CheckersPosition {
        if (positions[index].isEmpty) {
            val img = StorageManager.loadPositionImg(context, gameDir, index)
            positions[index] = Optional.of(CheckersPosition(this, index, img))
        }
        return positions[index].get()
    }

    override fun forEachPosition(action: (index: Int, position: CheckersPosition) -> Unit) {
        positions.forEachIndexed { index, optional ->
            action(index, getPosition(index))
        }
    }

    override fun forEachPosition(action: (position: CheckersPosition) -> Unit) {
        positions.forEachIndexed { index, optional -> action(getPosition(index)) }
    }
}