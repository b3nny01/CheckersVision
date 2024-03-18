package com.example.checkersvisionapp.controller.oldGames

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.checkersvisionapp.persistence.StorageManager
import kotlin.random.Random

class DataSource(private val context: Context) {
    private val initialGameList = gameViews()
    private val gamesLiveData = MutableLiveData(initialGameList)

    private fun gameViews(): List<CheckersGameView> {
        val games = mutableListOf<CheckersGameView>()
        StorageManager.loadCheckersGames(context).forEach { game ->
            games.add(
                CheckersGameView(
                    Random.nextLong(), game
                )
            )
        }
        return games
    }

    // Return game given an ID
    fun getGameViewForId(id: Long): CheckersGameView? {
        gamesLiveData.value?.let { games ->
            return games.firstOrNull { it.id == id }
        }
        return null
    }

    // Return current game list
    fun getGameViewsList(): LiveData<List<CheckersGameView>> {
        return gamesLiveData
    }

    // Delete game at position from game list
    fun removeGameView(position: Int) {
        val currentList = gamesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.removeAt(position)
            gamesLiveData.postValue(updatedList)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: DataSource? = null

        fun getDataSource(context: Context): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(context)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}