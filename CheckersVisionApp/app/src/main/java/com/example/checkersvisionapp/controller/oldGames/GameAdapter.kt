package com.example.checkersvisionapp.controller.oldGames

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.checkersvisionapp.R


class GameAdapter(private val onClick: (Int) -> Unit, private val onLongClick: (Int) -> Unit) :
    ListAdapter<CheckersGameView, GameAdapter.GameViewHolder>(GameDiffCallback){

    class GameViewHolder(itemView: View, private val onClick: (Int) -> Unit, private val onLongClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val gameName: TextView = itemView.findViewById(R.id.gameName)
        private var currentGame: CheckersGameView? = null

        init {
            itemView.setOnClickListener {
                currentGame?.let {
                    onClick(adapterPosition)
                }
            }
            itemView.setOnLongClickListener {
                currentGame?.let {
                    onLongClick(adapterPosition)
                }
                true
            }
        }

        // Bind game name
        fun bind(game: CheckersGameView) {
            currentGame = game
            gameName.text = game.name
        }
    }

    // Create and inflate view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from((parent.context)).inflate(R.layout.game_item, parent, false)
        return GameViewHolder(view, onClick, onLongClick)
    }

    // Get current game and use it to bind view
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)
        holder.bind(game)
    }

    // Give game id from element position
    override fun getItemId(position: Int): Long {
        val gamesList = this.currentList
        if (position in 0 until gamesList.size) {
            return gamesList[position].id
        }
        return  RecyclerView.NO_ID
    }
}

object GameDiffCallback : DiffUtil.ItemCallback<CheckersGameView>() {
    override fun areItemsTheSame(oldItem: CheckersGameView, newItem: CheckersGameView): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CheckersGameView, newItem: CheckersGameView): Boolean {
        return oldItem.id == newItem.id
    }
}