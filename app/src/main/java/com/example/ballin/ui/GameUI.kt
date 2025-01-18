package com.example.ballin.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ballin.model.Ball
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType

@Composable
fun GameScreen(
    ball: Ball,
    score: Int,
    grid: Array<Array<Cell>>,
    cellSize: Float,
    onPauseClick: () -> Unit,
    useCameraBackground: Boolean,
    themeColor: Int,
    lightLevel: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Wywołanie wspólnego tła poziomu
        LevelBackground(
            themeColor = themeColor,
            useCameraBackground = useCameraBackground
        )

        // Rysowanie siatki i obiektów gry
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    val cell = grid[row][col]
                    val x = col * cellSize
                    val y = row * cellSize

                    val color = when (cell.type) {
                        com.example.ballin.model.CellType.EMPTY -> Color.Transparent
                        com.example.ballin.model.CellType.OBSTACLE_RECTANGLE -> Color.Red
                        com.example.ballin.model.CellType.OBSTACLE_CIRCLE -> Color.Yellow
                        com.example.ballin.model.CellType.START -> Color.Green
                        com.example.ballin.model.CellType.GOAL -> Color.Blue
                        else -> Color.DarkGray
                    }

                    if (cell.type == com.example.ballin.model.CellType.OBSTACLE_CIRCLE) {
                        drawCircle(
                            color = color,
                            center = Offset(x + cellSize / 2, y + cellSize / 2),
                            radius = cellSize / 2
                        )
                    } else {
                        drawRect(
                            color = color,
                            topLeft = Offset(x, y),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }

            // Rysowanie kulki
            drawCircle(
                color = Color.Blue,
                radius = ball.radius,
                center = Offset(ball.x, ball.y)
            )
        }

        // Przycisk Pauza w prawym górnym rogu
        PauseButton(
            onClick = onPauseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Wynik i jasność na górze ekranu
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
            Text(
                text = "Jasność: ${lightLevel.toInt()} lx",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Kolor tła: #${Integer.toHexString(themeColor)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}




@Composable
fun PauseScreen(
    onResumeClick: () -> Unit,
    onExitClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    isCameraEnabled: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StyledButton(text = "Kontynuuj", onClick = onResumeClick)
            StyledButton(
                text = if (isCameraEnabled) "Wyłącz kamerę" else "Włącz kamerę",
                onClick = onToggleCameraClick
            )
            StyledButton(text = "Powrót do Menu", onClick = onExitClick)
        }
    }
}

@Composable
fun LevelBackground(
    themeColor: Int,
    useCameraBackground: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (useCameraBackground) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                context as ComponentActivity,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            Log.e("LevelBackground", "Error binding camera use cases", e)
                        }
                    }, ContextCompat.getMainExecutor(context))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Nakładka z przezroczystością
            drawRect(
                color = Color(themeColor).copy(alpha = 0.3f),
                size = size
            )
        }
    }
}


