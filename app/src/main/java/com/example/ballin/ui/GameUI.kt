package com.example.ballin.ui

import android.content.ComponentCallbacks
import androidx.activity.ComponentActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ballin.model.Ball
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun GameScreen(
    ball: Ball,
    score: Int,
    grid: Array<Array<Cell>>,
    cellSize: Float,
    onPauseClick: () -> Unit,
    useCameraBackground: Boolean,
    themeColor: Int, // ThemeColor w formacie ARGB
    lightLevel: Float // Dodano: aktualny poziom jasności
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                        val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            context as ComponentActivity,
                            cameraSelector,
                            preview
                        )
                    }, androidx.core.content.ContextCompat.getMainExecutor(context))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = androidx.compose.ui.graphics.Color(themeColor).copy(alpha = 0.3f),
                size = this.size
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    val cell = grid[row][col]
                    val x = col * cellSize
                    val y = row * cellSize

                    val color = when (cell.type) {
                        CellType.EMPTY -> Color.Transparent
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
            Button(onClick = onResumeClick) {
                Text(text = "Kontynuuj")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onToggleCameraClick) {
                Text(
                    text = if (isCameraEnabled) "Wyłącz kamerę" else "Włącz kamerę"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onExitClick) {
                Text(text = "Powrót do Menu")
            }
        }
    }
}
