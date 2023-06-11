package com.codingstuff.fiutoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddPlayersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.activity_add_players)
        val playerOne = findViewById<EditText>(R.id.playerOne)
        val startGameButton = findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            val getPlayerOneName = playerOne.text.toString()
            if (getPlayerOneName.isEmpty()) {
                Toast.makeText(
                    this@AddPlayersActivity,
                    getString(R.string.enter_player_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this@AddPlayersActivity, MainActivity::class.java)
                intent.putExtra("playerOne", getPlayerOneName)
                startActivity(intent)
            }
        }
    }
}