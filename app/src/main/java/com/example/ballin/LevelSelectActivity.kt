package com.example.ballin

import LevelSelectionScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ballin.model.LevelManager
import com.example.ballin.ui.theme.BallinTheme

class LevelSelectActivity : ComponentActivity() {

    private lateinit var levelManager: LevelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        levelManager = LevelManager(this)
        levelManager.loadLevelsFromJson("levels.json")

        setContent {
            BallinTheme {
                LevelSelectionScreen(
                    levelManager = levelManager,
                    onLevelSelected = { levelId ->
                        val intent = Intent(this, GameActivity::class.java).apply {
                            putExtra("LEVEL_ID", levelId) // Wysyłamy ID poziomu
                        }
                        startActivity(intent)
                    }
                )

            }
        }
    }
}

