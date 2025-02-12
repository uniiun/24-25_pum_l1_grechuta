package com.example.ballin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.material.button.MaterialButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Przycisk START
        val startButton = findViewById<MaterialButton>(R.id.startGameButton)
        startButton.setOnClickListener {
            // Przejdź do gry
            val intent = Intent(this,  LevelSelectActivity::class.java)
            startActivity(intent)
        }

        // Przycisk WYBIERZ KOLOR
        val selectColorButton = findViewById<MaterialButton>(R.id.selectColorButton)
        selectColorButton.setOnClickListener {
            // Przejdź do wyboru koloru
            val intent = Intent(this, ColorPickerActivity::class.java)
            startActivity(intent)
        }
    }
}

@Composable
fun MainMenu(onStartGameClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onStartGameClick,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Start Game")
            }
        }
    }
}
