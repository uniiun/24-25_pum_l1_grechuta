package com.example.ballin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.example.ballin.model.Ball
import com.example.ballin.ui.theme.BallinTheme

class GameActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null

    private var ball by mutableStateOf(Ball(x = 540f, y = 960f, dx = 0f, dy = 0f, radius = 50f))
    private var rotationX by mutableStateOf(0f)
    private var rotationY by mutableStateOf(0f)
    private var score by mutableStateOf(0)

    private val gravityFactor = 0.5f // Współczynnik wpływu żyroskopu
    private val dampingFactor = 0.98f // Współczynnik tłumienia prędkości

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            BallinTheme {
                GameScreen(
                    ball = ball,
                    score = score,
                    onPauseClick = { pauseGame() },
                    onRestartClick = { restartGame() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            // Aktualizuj dane żyroskopu
            rotationX = event.values[1]
            rotationY = event.values[0]

            // Aktualizacja prędkości z uwzględnieniem "grawitacji"
            ball.dx += rotationX * gravityFactor
            ball.dy += rotationY * gravityFactor

            // Zastosowanie tłumienia prędkości
            ball.dx *= dampingFactor
            ball.dy *= dampingFactor

            // Aktualizacja pozycji kulki
            ball.updatePosition(
                width = resources.displayMetrics.widthPixels,
                height = resources.displayMetrics.heightPixels,
                dampingFactor = dampingFactor
            )

            // Aktualizuj wynik na podstawie pozycji kulki
            score = calculateScore(ball.x, ball.y)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nie jest wymagane w tej implementacji
    }

    private fun pauseGame() {
        // Logika pauzy gry
    }

    private fun restartGame() {
        // Resetowanie gry
        score = 0
        ball = Ball(
            x = resources.displayMetrics.widthPixels / 2f,
            y = resources.displayMetrics.heightPixels / 2f,
            dx = 0f,
            dy = 0f,
            radius = 50f
        )
    }

    private fun calculateScore(x: Float, y: Float): Int {
        // Przykładowa logika przeliczania wyniku
        return ((x + y) / 10).toInt()
    }
}

@Composable
fun GameScreen(
    ball: Ball,
    score: Int,
    onPauseClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas gry
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Red,
                radius = ball.radius,
                center = androidx.compose.ui.geometry.Offset(ball.x, ball.y)
            )
        }

        // Wyświetlanie wyniku
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wynik: $score",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Pozycja kulki X: ${ball.x.toInt()}")
            Text(text = "Pozycja kulki Y: ${ball.y.toInt()}")
        }

        // Przycisk Pauzy
        Button(
            onClick = onPauseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Pauza")
        }

        // Przycisk Restartu
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
            ball = Ball(x = 540f, y = 960f, dx = 0f, dy = 0f, radius = 50f),
            score = 0,
            onPauseClick = { /* Podgląd pauzy */ },
            onRestartClick = { /* Podgląd restartu */ }
        )
    }
}
