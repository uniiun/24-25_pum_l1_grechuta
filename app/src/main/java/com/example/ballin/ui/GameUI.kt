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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ballin.R
import com.example.ballin.model.Ball
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType


@Composable
fun rememberScaledBitmaps(cellSize: Float, ballDiameter: Float): Map<Int, ImageBitmap> {
    val context = LocalContext.current
    return remember(cellSize, ballDiameter) {
        mapOf(
            // Przeszkody oraz elementy planszy:
            R.drawable.rock to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.rock)!!
            ).scaleToSize(cellSize),
            R.drawable.bush to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.bush)!!
            ).scaleToSize(cellSize),
            R.drawable.start to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.start)!!
            ).scaleToSize(cellSize),
            R.drawable.goal to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.goal)!!
            ).scaleToSize(cellSize),
            // Kulki – użytkownik wybiera kolor spośród dostępnych:
            R.drawable.benson to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.benson)!!
            ).scaleToSize(ballDiameter),
            R.drawable.bluson to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.bluson)!!
            ).scaleToSize(ballDiameter),
            R.drawable.greenson to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.greenson)!!
            ).scaleToSize(ballDiameter),
            R.drawable.roson to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.roson)!!
            ).scaleToSize(ballDiameter),
            R.drawable.yellson to drawableToImageBitmap(
                ContextCompat.getDrawable(context, R.drawable.yellson)!!
            ).scaleToSize(ballDiameter)
        )
    }
}

@Composable
fun GameScreen(
    ball: Ball,
    score: Long,
    grid: Array<Array<Cell>>,
    cellSize: Float,
    onPauseClick: () -> Unit,
    useCameraBackground: Boolean,
    themeColor: Int,
    lightLevel: Float,
    selectedBallResource: Int
) {
    val context = LocalContext.current

    // Obliczamy średnicę kulki (2 * radius)
    val ballDiameter = ball.radius * 2
    // Cache'ujemy bitmapy przeszkód oraz kulki – używamy wspólnego cache'a
    val scaledBitmaps = rememberScaledBitmaps(cellSize, ballDiameter)

    Box(modifier = Modifier.fillMaxSize()) {
        LevelBackground(
            themeColor = themeColor,
            useCameraBackground = useCameraBackground
        )
        // Rysowanie planszy – używamy wcześniej zcache'owanych bitmap
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Iteracja po komórkach planszy
            for (row in grid.indices) {
                for (col in grid[row].indices) {
                    val cell = grid[row][col]
                    val topLeft = Offset(col * cellSize, row * cellSize)
                    when (cell.type) {
                        CellType.OBSTACLE_RECTANGLE -> {
                            scaledBitmaps[R.drawable.rock]?.let { bitmap ->
                                drawImage(bitmap, topLeft)
                            }
                        }
                        CellType.OBSTACLE_CIRCLE -> {
                            scaledBitmaps[R.drawable.bush]?.let { bitmap ->
                                drawImage(bitmap, topLeft)
                            }
                        }
                        CellType.START -> {
                            scaledBitmaps[R.drawable.start]?.let { bitmap ->
                                drawImage(bitmap, topLeft)
                            }
                        }
                        CellType.GOAL -> {
                            scaledBitmaps[R.drawable.goal]?.let { bitmap ->
                                drawImage(bitmap, topLeft)
                            }
                        }
                        else -> { /* Nic nie rysujemy */ }
                    }
                }
            }

            // Rysowanie kulki – wybieramy bitmapę na podstawie resource ID
            scaledBitmaps[selectedBallResource]?.let { ballBitmap ->
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawBitmap(
                        ballBitmap.asAndroidBitmap(),
                        ball.x - ball.radius,
                        ball.y - ball.radius,
                        null
                    )
                }
            }
        }
        PauseButton(
            onClick = onPauseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val formattedScore = formatTimeMsToMMSS(score)
            Text("Wynik: $formattedScore", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Jasność: ${lightLevel.toInt()} lx", style = MaterialTheme.typography.bodyMedium)
            Text("Kolor tła: #${Integer.toHexString(themeColor)}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun formatTimeMsToMMSS(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

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
