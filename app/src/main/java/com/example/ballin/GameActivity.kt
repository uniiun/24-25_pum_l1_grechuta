package com.example.ballin

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.example.ballin.model.Ball
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType
import com.example.ballin.model.Level
import com.example.ballin.model.LevelManager
import com.example.ballin.model.ObstacleType
import com.example.ballin.ui.GameScreen
import com.example.ballin.ui.PauseScreen
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
    private var isPaused by mutableStateOf(false)
    private var useCameraBackground by mutableStateOf(false)

    private val gravityFactor = 0.5f
    private val dampingFactor = 0.98f

    private val gridWidth = 10
    private val gridHeight = 20
    private var cellSize by mutableStateOf(0f)

    private var themeColor by mutableStateOf(Color.Transparent.toArgb())

    private var lightSensor: Sensor? = null
    private var lightLevel by mutableStateOf(0f)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            } else {
                Log.e("GameActivity", "Kamera nie została przyznana")
            }
        }

    private val grid: Array<Array<Cell>> = Array(gridHeight) {
        Array(gridWidth) { Cell() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val levelId = intent.getIntExtra("LEVEL_ID", -1)
        if (levelId == -1) {
            finish()
            return
        }

        levelManager = LevelManager(this)
        levelManager.loadLevelsFromJson("levels.json")

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        cellSize = minOf(screenWidth / gridWidth, screenHeight / gridHeight)

        val currentLevel = levelManager.getLevelById(levelId)
        if (currentLevel != null) {
            setupLevel(currentLevel)
            levelManager.setCurrentLevelById(currentLevel.id)  // Ustawienie bieżącego poziomu na podstawie ID
            updateThemeColor()
        } else {
            finish()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        Log.d("GameActivity", "Light sensor available: ${lightSensor != null}")

        levelManager.addLevelChangeListener {
            updateThemeColor()
        }

        setContent {
            BallinTheme {
                if (isPaused) {
                    PauseScreen(
                        onResumeClick = { resumeGame() },
                        onExitClick = { finish() },
                        onToggleCameraClick = { useCameraBackground = !useCameraBackground },
                        isCameraEnabled = useCameraBackground
                    )
                } else {
                    GameScreen(
                        ball = ball,
                        score = score,
                        grid = grid,
                        cellSize = cellSize,
                        onPauseClick = { pauseGame() },
                        useCameraBackground = useCameraBackground,
                        themeColor = themeColor,
                        lightLevel = lightLevel
                    )
                }
            }
        }
    }

    fun updateThemeColor() {
        val nextLevel = levelManager.getCurrentLevel()
        themeColor = when(nextLevel?.id) {
            1 -> adjustColorBrightness(0xFFFF0000.toInt(), lightLevel) // czerwony
            2 -> adjustColorBrightness(0xFF00FF00.toInt(), lightLevel) // zielony
            3 -> adjustColorBrightness(0xFF0000FF.toInt(), lightLevel) // niebieski
            4 -> adjustColorBrightness(0xFFFFFF00.toInt(), lightLevel) // żółty
            else -> Color.Transparent.toArgb()
        }
        Log.d("GameActivity", "Forced themeColor for level ${nextLevel?.id}: ${Integer.toHexString(themeColor)}")
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build()
            val previewView = PreviewView(this).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupLevel(level: Level) {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                grid[row][col].type = CellType.EMPTY
            }
        }

        ball.x = level.startPosition.x * cellSize + cellSize / 2
        ball.y = level.startPosition.y * cellSize + cellSize / 2

        grid[level.goalPosition.y][level.goalPosition.x].type = CellType.GOAL

        for (obstacle in level.obstacles) {
            when (obstacle.type) {
                ObstacleType.RECTANGLE -> grid[obstacle.y][obstacle.x].type = CellType.OBSTACLE_RECTANGLE
                ObstacleType.CIRCLE -> grid[obstacle.y][obstacle.x].type = CellType.OBSTACLE_CIRCLE
            }
        }

        grid[level.startPosition.y][level.startPosition.x].type = CellType.START
    }

    override fun onResume() {
        super.onResume()
        if (!isPaused) {
            gyroscopeSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
            lightSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isPaused && event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
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
        } else if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            lightLevel = event.values[0]
            Log.d("GameActivity", "Light sensor changed: $lightLevel lx")
            val currentLevelColor = levelManager.getCurrentLevel()?.themeColor ?: Color.White.toArgb()
            themeColor = adjustColorBrightness(currentLevelColor, lightLevel)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun adjustColorBrightness(baseColor: Int, lightLevel: Float): Int {
        val color = Color(baseColor)
        val brightnessFactor = when {
            lightLevel > 10000 -> 1.2f
            lightLevel > 5000 -> 1.1f
            lightLevel > 1000 -> 0.9f
            else -> 0.7f
        }

        val red = (color.red * brightnessFactor).coerceIn(0f, 1f)
        val green = (color.green * brightnessFactor).coerceIn(0f, 1f)
        val blue = (color.blue * brightnessFactor).coerceIn(0f, 1f)

        val adjustedColor = Color(red, green, blue).toArgb()
        Log.d("AdjustColor", "BaseColor: ${Integer.toHexString(baseColor)}, LightLevel: $lightLevel, " +
                "BrightnessFactor: $brightnessFactor, AdjustedColor: ${Integer.toHexString(adjustedColor)}")
        return adjustedColor
    }

    private fun checkCollision() {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val cell = grid[row][col]
                val x = col * cellSize
                val y = row * cellSize

                when (cell.type) {
                    CellType.GOAL -> {
                        if (ball.x + ball.radius > x && ball.x - ball.radius < x + cellSize &&
                            ball.y + ball.radius > y && ball.y - ball.radius < y + cellSize) {
                            onLevelComplete()
                        }
                    }
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

    private fun onLevelComplete() {
        levelManager.nextLevel()
        val nextLevel = levelManager.getCurrentLevel()
        if (nextLevel != null) {
            setupLevel(nextLevel)
            updateThemeColor() // Ustawienie koloru motywu po załadowaniu nowego poziomu
        } else {
            finish()
            // Tutaj możesz wyświetlić komunikat o ukończeniu gry
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

    private fun pauseGame() {
        isPaused = true
        sensorManager.unregisterListener(this)
    }

    private fun resumeGame() {
        isPaused = false
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun calculateThemeColor(lightLevel: Float): Int {
        return if (lightLevel > 10000) {
            Color(0xFFFFF59D).toArgb()
        } else if (lightLevel > 5000) {
            Color(0xFF90CAF9).toArgb()
        } else {
            Color(0xFF37474F).toArgb()
        }
    }

    private fun calculateScore(x: Float, y: Float): Int {
        return ((x + y) / 10).toInt()
    }
}
