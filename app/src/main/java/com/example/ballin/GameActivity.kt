package com.example.ballin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ballin.ui.theme.BallinTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BallinTheme {
                GameScreen(
                    onPauseClick = { /* Add pause logic here */ },
                    onRestartClick = { /* Add restart logic here */ }
                )
            }
        }
    }
}

@Composable
fun GameScreen(
    onPauseClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    var score by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Red,
                radius = 50f,
                center = center
            )
        }

        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        Button(
            onClick = onPauseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Pause")
        }

        Button(
            onClick = onRestartClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Restart")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    BallinTheme {
        GameScreen(
            onPauseClick = { /* Preview pause logic */ },
            onRestartClick = { /* Preview restart logic */ }
        )
    }
}
