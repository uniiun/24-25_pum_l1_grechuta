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
import com.example.ballin.model.Obstacle
import com.example.ballin.model.ObstacleType
import com.example.ballin.model.Level
import com.example.ballin.model.LevelManager
import com.example.ballin.ui.theme.BallinTheme
import kotlin.math.pow
import kotlin.math.sqrt

class GameActivity : ComponentActivity(), SensorEventListener {

    private lateinit var levelManager: LevelManager
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

        levelManager = LevelManager(this)
        levelManager.loadLevelsFromJson("levels.json")

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        cellSize = minOf(screenWidth / gridWidth, screenHeight / gridHeight)

        val currentLevel = levelManager.getCurrentLevel()
        if (currentLevel != null) {
            setupLevel(currentLevel)
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            BallinTheme {
                GameScreen(
                    ball = ball,
                    score = score,
                    grid = grid,
                    cellSize = cellSize,
                    onPauseClick = { pauseGame() }
                )
            }
        }
    }

    private fun setupLevel(level: Level) {
        // Wyczyszczenie siatki
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                grid[row][col].type = CellType.EMPTY
            }
        }

        // Ustawienie pozycji startowej kulki
        ball.x = level.startPosition.x * cellSize + cellSize / 2
        ball.y = level.startPosition.y * cellSize + cellSize / 2

        // Ustawienie mety
        grid[level.goalPosition.y][level.goalPosition.x].type = CellType.GOAL

        // Dodanie przeszkód
        for (obstacle in level.obstacles) {
            when (obstacle.type) {
                ObstacleType.RECTANGLE -> grid[obstacle.y][obstacle.x].type = CellType.OBSTACLE_RECTANGLE
                ObstacleType.CIRCLE -> grid[obstacle.y][obstacle.x].type = CellType.OBSTACLE_CIRCLE
                else -> grid[obstacle.y][obstacle.x].type = CellType.OBSTACLE
            }
        }

        // Ustawienie startu
        grid[level.startPosition.y][level.startPosition.x].type = CellType.START
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

            checkCollision()

            score = calculateScore(ball.x, ball.y)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkCollision() {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val cell = grid[row][col]
                val x = col * cellSize
                val y = row * cellSize

                when (cell.type) {
                    CellType.OBSTACLE_RECTANGLE -> {
                        if (ball.x + ball.radius > x && ball.x - ball.radius < x + cellSize &&
                            ball.y + ball.radius > y && ball.y - ball.radius < y + cellSize) {
                            handleRectangleCollision(x, y)
                        }
                    }
                    CellType.OBSTACLE_CIRCLE -> {
                        val circleCenterX = x + cellSize / 2
                        val circleCenterY = y + cellSize / 2
                        val distance = sqrt(
                            (ball.x - circleCenterX).pow(2) + (ball.y - circleCenterY).pow(2)
                        )
                        if (distance < ball.radius + cellSize / 2) {
                            handleCircleCollision(circleCenterX, circleCenterY)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleRectangleCollision(rectX: Float, rectY: Float) {
        if (ball.x < rectX || ball.x > rectX + cellSize) {
            ball.dx = -ball.dx
        }
        if (ball.y < rectY || ball.y > rectY + cellSize) {
            ball.dy = -ball.dy
        }
        ball.dx *= dampingFactor
        ball.dy *= dampingFactor
    }

    private fun handleCircleCollision(circleX: Float, circleY: Float) {
        val dx = ball.x - circleX
        val dy = ball.y - circleY
        val distance = sqrt(dx.pow(2) + dy.pow(2))

        if (distance != 0.0f) {
            val nx = dx / distance
            val ny = dy / distance
            val dotProduct = ball.dx * nx + ball.dy * ny

            ball.dx -= 2 * dotProduct * nx
            ball.dy -= 2 * dotProduct * ny
        }
        ball.dx *= dampingFactor
        ball.dy *= dampingFactor
    }

    private fun pauseGame() {}

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
    onPauseClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    val cell = grid[row][col]
                    val x = col * cellSize
                    val y = row * cellSize

                    val color = when (cell.type) {
                        CellType.EMPTY -> Color.LightGray
                        CellType.OBSTACLE_RECTANGLE -> Color.Red
                        CellType.OBSTACLE_CIRCLE -> Color.Yellow
                        CellType.START -> Color.Green
                        CellType.GOAL -> Color.Blue
                        else -> Color.DarkGray
                    }

                    if (cell.type == CellType.OBSTACLE_CIRCLE) {
                        drawCircle(
                            color = color,
                            center = androidx.compose.ui.geometry.Offset(x + cellSize / 2, y + cellSize / 2),
                            radius = cellSize / 2
                        )
                    } else {
                        drawRect(
                            color = color,
                            topLeft = androidx.compose.ui.geometry.Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                        )
                    }
                }
            }

            drawCircle(
                color = Color.Blue,
                radius = ball.radius,
                center = androidx.compose.ui.geometry.Offset(ball.x, ball.y)
            )
        }

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
        }

        Button(
            onClick = onPauseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Pauza")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    val mockGrid = Array(15) { Array(10) { Cell() } }
    mockGrid[14][5].type = CellType.START
    mockGrid[0][5].type = CellType.GOAL
    mockGrid[7][3].type = CellType.OBSTACLE_RECTANGLE

    BallinTheme {
        GameScreen(
            ball = Ball(x = 540f, y = 960f, dx = 0f, dy = 0f, radius = 50f),
            score = 0,
            grid = mockGrid,
            cellSize = 100f,
            onPauseClick = {}
        )
    }
}
