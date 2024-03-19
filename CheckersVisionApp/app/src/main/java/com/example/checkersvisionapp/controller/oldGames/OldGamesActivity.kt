package com.example.checkersvisionapp.controller.oldGames

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.checkersvisionapp.R
import com.example.checkersvisionapp.controller.PredictionActivity
import com.example.checkersvisionapp.persistence.StorageManager

class OldGamesActivity : AppCompatActivity() {

    private lateinit var gamesAdapter: GameAdapter
    private lateinit var dataSource: DataSource
    private lateinit var recyclerView: RecyclerView

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_old_games)

        // getting layout elements
        recyclerView = findViewById(R.id.recycler_view)

        dataSource = DataSource(this)


        gamesAdapter = GameAdapter({ position -> onItemClick(position) }, { position -> onLongItemClick(position) })
        recyclerView.adapter = gamesAdapter

        dataSource.getGameViewsList().observe(this) {
            it?.let {
                gamesAdapter.submitList(it as MutableList<CheckersGameView>)
            }
        }

        //registerForContextMenu(recyclerView)
    }

    private fun onItemClick(position:Int) {
        val gameId = gamesAdapter.getItemId(position)
        val game = dataSource.getGameViewForId(gameId)
        val intent = Intent(this, PredictionActivity()::class.java)
        intent.putExtra("gameName", game?.name)
        startActivity(intent)
    }

    private fun onLongItemClick(position: Int) {
        val gameToDeleteId = gamesAdapter.getItemId(position)
        val gameToDelete = dataSource.getGameViewForId(gameToDeleteId)

        if (gameToDelete != null) {
            // Alert dialog to confirm or dismiss game deletion
            AlertDialog.Builder(this)
                .setMessage("Sei sicuro di voler cancellare questo game?")
                .setPositiveButton("Conferma") { dialog, _ ->
                    deleteGame(gameToDelete, position)
                    dialog.dismiss()
                }
                .setNegativeButton("Annulla") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    // Delete game from datasource
    private fun deleteGame(gameToDelete: CheckersGameView, position: Int) {
        val result = StorageManager.deleteCheckersGame(this, gameToDelete.name)
        if (result) {
            dataSource.removeGameView(position)
            Toast.makeText(this, "Game eliminato con successo", Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, "Impossibile eliminare game", Toast.LENGTH_SHORT).show()
    }
}