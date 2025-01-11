package com.example.ballin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                LevelSelectionScreen(levelManager = levelManager, onLevelSelected = { levelIndex ->
                    val intent = Intent(this, GameActivity::class.java).apply {
                        putExtra("LEVEL_INDEX", levelIndex)
                    }
                    startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun LevelSelectionScreen(levelManager: LevelManager, onLevelSelected: (Int) -> Unit) {
    val levels = remember { levelManager.getLevels() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            levels.forEachIndexed { index, level ->
                Button(
                    onClick = { onLevelSelected(index) },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Level ${index + 1}")
                }
            }
        }
    }
}
