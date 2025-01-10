package com.example.ballin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.ballin.ui.theme.BallinTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BallinTheme {
                GameScreen()
            }
        }
    }
}

@Composable
fun GameScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Tu można dodać logikę gry, np. komponenty Canvas do rysowania
        Text(
            text = "Welcome to the Game!",
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    BallinTheme {
        GameScreen()
    }
}
