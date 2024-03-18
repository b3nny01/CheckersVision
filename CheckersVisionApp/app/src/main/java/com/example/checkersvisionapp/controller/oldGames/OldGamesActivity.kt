package com.example.checkersvisionapp.controller.oldGames

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.checkersvisionapp.R
import com.example.checkersvisionapp.controller.MainActivity
import com.example.checkersvisionapp.controller.PredictionActivity
import com.example.checkersvisionapp.persistence.StorageManager
import java.io.File

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

        dataSource = DataSource.getDataSource(this)


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

    /* Create and inflate context menu
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        val info = menuInfo as ContextMenuRecyclerView.RecyclerContextMenuInfo

        if (info != null) {
            // Get element information from RecyclerView
            val itemPosition = info.position
            // Get element id from adapter
            val itemId = gamesAdapter.getItemId(itemPosition)


            // Set menuInfo with element id
            menu.setHeaderTitle("Menu per l'elemento $itemId")
            menu.add(Menu.NONE, R.id.ctx_menu_show_prediction, Menu.NONE, R.string.show_game)
            menu.add(Menu.NONE, R.id.ctx_menu_delete, Menu.NONE, R.string.delete)
            //menuInfo.targetView.setTag(R.id.menu_item_id, itemId)
        }
    }

    // Choices out of the menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as ContextMenuRecyclerView.RecyclerContextMenuInfo
        return when (item.itemId) {
            R.id.show_game -> {
                val intent = Intent(this, PredictionActivity()::class.java)
                startActivity(intent)
                true
            }
            R.id.delete_game -> {
                val gameToDeleteId = gamesAdapter.getItemId(info.position)
                val gameToDelete = dataSource.getGameForId(gameToDeleteId)
                if (gameToDelete != null) {
                    val dirName = gameToDelete.name
                    val dirToDelete = File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        dirName
                    )
                    deleteDirectory(dirToDelete)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
    */

    // Delete game from datasource
    private fun deleteGame(gameToDelete: CheckersGameView, position: Int) {
        val result = StorageManager.deleteCheckersGame(this, gameToDelete.name)
        if (result) {
            dataSource.removeGameView(position)
            Toast.makeText(this, "Game eliminato con successo", Toast.LENGTH_LONG).show()
        } else Toast.makeText(this, "Impossibile eliminare game", Toast.LENGTH_SHORT).show()
    }

    // Delete directory from local storage
    private fun deleteDirectory(directory: File): Boolean {
        if (directory.exists()) {
            if (directory.deleteRecursively()) {
                Toast.makeText(this, "Game eliminato con successo", Toast.LENGTH_LONG).show()
                return true
            } else {
                Toast.makeText(this, "Impossibile eliminare game", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Il game non esiste", Toast.LENGTH_SHORT).show()
        }
        return false
    }
}