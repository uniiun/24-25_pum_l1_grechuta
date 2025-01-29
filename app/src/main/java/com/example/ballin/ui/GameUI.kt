package com.example.ballin.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ballin.R
import com.example.ballin.model.Ball
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas


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
    val context = LocalContext.current // Pobranie kontekstu poza Canvas

    Box(modifier = Modifier.fillMaxSize()) {
        // Tło poziomu
        LevelBackground(
            themeColor = themeColor,
            useCameraBackground = useCameraBackground
        )

        // Rysowanie siatki i obiektów gry
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    val cell = grid[row][col]
                    val topLeft = Offset(col * cellSize, row * cellSize)

                    when (cell.type) {
                        CellType.OBSTACLE_RECTANGLE -> {
                            val drawable = ContextCompat.getDrawable(context, R.drawable.rock)
                            drawable?.let {
                                val scaledBitmap = drawableToImageBitmap(it).scaleToSize(cellSize)
                                drawImage(scaledBitmap, topLeft)
                            }
                        }
                        CellType.OBSTACLE_CIRCLE -> {
                            val drawable = ContextCompat.getDrawable(context, R.drawable.bush)
                            drawable?.let {
                                val scaledBitmap = drawableToImageBitmap(it).scaleToSize(cellSize)
                                drawImage(scaledBitmap, topLeft)
                            }
                        }
                        CellType.START -> {
                            val drawable = ContextCompat.getDrawable(context, R.drawable.start)
                            drawable?.let {
                                val scaledBitmap = drawableToImageBitmap(it).scaleToSize(cellSize)
                                drawImage(scaledBitmap, topLeft)
                            }
                        }
                        CellType.GOAL -> {
                            val drawable = ContextCompat.getDrawable(context, R.drawable.goal)
                            drawable?.let {
                                val scaledBitmap = drawableToImageBitmap(it).scaleToSize(cellSize)
                                drawImage(scaledBitmap, topLeft)
                            }
                        }
                        else -> {}
                    }
                }
            }

            // Rysowanie kulki gracza
            ball.drawable?.let { drawable ->
                drawIntoCanvas { canvas ->
                    val bitmap = drawableToImageBitmap(drawable).scaleToSize(ball.radius * 2)
                    canvas.nativeCanvas.drawBitmap(
                        bitmap.asAndroidBitmap(),
                        ball.x - ball.radius,
                        ball.y - ball.radius,
                        null
                    )
                }
            }
        }

        // Przycisk Pauza
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
            Text("Wynik: $score", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Jasność: ${lightLevel.toInt()} lx", style = MaterialTheme.typography.bodyMedium)
            Text("Kolor tła: #${Integer.toHexString(themeColor)}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
// Funkcja konwertująca Drawable do ImageBitmap
fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
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

@Composable
fun PauseScreen(
    onResumeClick: () -> Unit,
    onExitClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    isCameraEnabled: Boolean,
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

fun ImageBitmap.scaleToSize(targetSize: Float): ImageBitmap {
    val bitmap = this.asAndroidBitmap()
    val scaledBitmap = Bitmap.createScaledBitmap(
        bitmap,
        targetSize.toInt(),
        targetSize.toInt(),
        true
    )
    return scaledBitmap.asImageBitmap()
}