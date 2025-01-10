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
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType
import com.example.ballin.ui.theme.BallinTheme

class GameActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null

    private var ball by mutableStateOf(Ball(x = 0f, y = 0f, dx = 0f, dy = 0f, radius = 50f))
    private var rotationX by mutableStateOf(0f)
    private var rotationY by mutableStateOf(0f)
    private var score by mutableStateOf(0)

    private val gravityFactor = 0.5f
    private val dampingFactor = 0.98f

    private val gridWidth = 10
    private val gridHeight = 20
    private var cellSize by mutableStateOf(0f)

    private val grid: Array<Array<Cell>> = Array(gridHeight) {
        Array(gridWidth) { Cell() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setupGrid()

        // Znalezienie pozycji miejsca startowego
        val startCell = grid.flatten().firstOrNull { it.type == CellType.START }
        val startRow = grid.indexOfFirst { row -> row.contains(startCell) }
        val startCol = grid[startRow].indexOf(startCell)

        ball = Ball(
            x = startCol * cellSize + cellSize / 2,
            y = startRow * cellSize + cellSize / 2,
            dx = 0f,
            dy = 0f,
            radius = 50f
        )

        setContent {
            BallinTheme {
                val screenWidth = resources.displayMetrics.widthPixels.toFloat()
                val screenHeight = resources.displayMetrics.heightPixels.toFloat()

                cellSize = minOf(screenWidth / gridWidth, screenHeight / gridHeight)

                GameScreen(
                    ball = ball,
                    score = score,
                    grid = grid,
                    cellSize = cellSize,
                    onPauseClick = { pauseGame() },
                    onRestartClick = { restartGame() }
                )
            }
        }
    }


    private fun setupGrid() {
        // Ustawienie startu i mety
        grid[gridHeight - 1][gridWidth / 2].type = CellType.START
        grid[0][gridWidth / 2].type = CellType.GOAL

        // Dodanie przeszkód
        grid[5][4].type = CellType.OBSTACLE
        grid[8][6].type = CellType.OBSTACLE
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
            rotationX = event.values[1]
            rotationY = event.values[0]

            ball.dx += rotationX * gravityFactor
            ball.dy += rotationY * gravityFactor

            ball.dx *= dampingFactor
            ball.dy *= dampingFactor

            ball.updatePosition(
                gridWidth = gridWidth,
                gridHeight = gridHeight,
                cellSize = cellSize,
                dampingFactor = dampingFactor
            )

            score = calculateScore(ball.x, ball.y)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun pauseGame() {}

    private fun restartGame() {
        score = 0
        // Znalezienie pozycji miejsca startowego
        val startCell = grid.flatten().firstOrNull { it.type == CellType.START }
        val startRow = grid.indexOfFirst { row -> row.contains(startCell) }
        val startCol = grid[startRow].indexOf(startCell)

        ball = Ball(
            x = startCol * cellSize + cellSize / 2,
            y = startRow * cellSize + cellSize / 2,
            dx = 0f,
            dy = 0f,
            radius = 50f
        )
    }


    private fun calculateScore(x: Float, y: Float): Int {
        return ((x + y) / 10).toInt()
    }
}

@Composable
fun GameScreen(
    ball: Ball,
    score: Int,
    grid: Array<Array<Cell>>,
    cellSize: Float,
    onPauseClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas gry
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Renderowanie siatki
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    val cell = grid[row][col]
                    val x = col * cellSize
                    val y = row * cellSize

                    val color = when (cell.type) {
                        CellType.EMPTY -> Color.LightGray
                        CellType.OBSTACLE -> Color.DarkGray
                        CellType.START -> Color.Green
                        CellType.GOAL -> Color.Red
                    }

                    drawRect(
                        color = color,
                        topLeft = androidx.compose.ui.geometry.Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }
            }

            // Rysowanie kulki
            drawCircle(
                color = Color.Blue,
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
    val mockGrid = Array(15) { Array(10) { Cell() } }
    mockGrid[14][5].type = CellType.START
    mockGrid[0][5].type = CellType.GOAL
    mockGrid[7][3].type = CellType.OBSTACLE

    BallinTheme {
        GameScreen(
            ball = Ball(x = 540f, y = 960f, dx = 0f, dy = 0f, radius = 50f),
            score = 0,
            grid = mockGrid,
            cellSize = 100f,
            onPauseClick = { /* Podgląd pauzy */ },
            onRestartClick = { /* Podgląd restartu */ }
        )
    }
}
