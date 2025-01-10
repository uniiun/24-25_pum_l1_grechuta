package com.example.ballin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ballin.ui.theme.BallinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BallinTheme {
                MainScreen(onStartGameClick = {
                    val intent = Intent(this, GameActivity::class.java)
                    startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun MainScreen(onStartGameClick: () -> Unit) {
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

            Button(onClick = { /* Dodaj logikę wyjścia lub inną funkcję */ }) {
                Text(text = "Exit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BallinTheme {
        MainScreen(onStartGameClick = {})
    }
}
