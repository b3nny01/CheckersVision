package com.example.checkersvisionapp.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.checkersvisionapp.R
import com.example.checkersvisionapp.controller.oldGames.OldGamesActivity

class MainActivity : AppCompatActivity() {

    private lateinit var newGameBtn: Button
    private lateinit var myGamesBtn: Button

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // getting layout elements
        newGameBtn = findViewById(R.id.newGameBtn)
        myGamesBtn = findViewById(R.id.myGamesBtn)


        // newGameBtn initialization
        newGameBtn.setOnClickListener {
            val intent = Intent(this, NewGameActivity::class.java)
            startActivity(intent)
        }

        // oldGamesBtn initialization
        myGamesBtn.setOnClickListener {
            val intent = Intent(this, OldGamesActivity::class.java)
            startActivity(intent)
        }

    }

}