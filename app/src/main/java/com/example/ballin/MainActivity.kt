package com.example.ballin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ballin.ui.theme.BallinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BallinTheme {
                MainMenu(onStartGameClick = {
                    val intent = Intent(this, LevelSelectActivity::class.java)
                    startActivity(intent)
                })
            }
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
