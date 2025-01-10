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
import com.example.ballin.ui.theme.BallinTheme

class GameActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null
    private var rotationX by mutableStateOf(0f)
    private var rotationY by mutableStateOf(0f)
    private var score by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            BallinTheme {
                GameScreen(
                    rotationX = rotationX,
                    rotationY = rotationY,
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
            rotationX = event.values[0]
            rotationY = event.values[1]

            // Aktualizuj logikę gry na podstawie danych żyroskopu
            score = calculateScore(rotationX, rotationY)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nie jest wymagane w tej implementacji
    }

    private fun pauseGame() {
        // Logika pauzy gry
    }

    private fun restartGame() {
        // Logika restartu gry
        score = 0
    }

    private fun calculateScore(rotationX: Float, rotationY: Float): Int {
        // Przykładowa logika przeliczania wyniku na podstawie żyroskopu
        return (rotationX * 10 + rotationY * 10).toInt()
    }
}

@Composable
fun GameScreen(
    rotationX: Float,
    rotationY: Float,
    score: Int,
    onPauseClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas dla gry
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Red,
                radius = 50f,
                center = center
            )
        }

        // Wyświetlanie wyników i danych żyroskopu
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Rotation X: $rotationX")
            Text(text = "Rotation Y: $rotationY")
        }

        // Przycisk Pauzy
        Button(
            onClick = onPauseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Pause")
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
            rotationX = 0f,
            rotationY = 0f,
            score = 0,
            onPauseClick = { /* Testowy podgląd pauzy */ },
            onRestartClick = { /* Testowy podgląd restartu */ }
        )
    }
}
