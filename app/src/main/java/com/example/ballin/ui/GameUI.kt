package com.example.ballin.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    originalCellSize: Float, // oryginalny rozmiar komórki (w pikselach) z poziomu
    onPauseClick: () -> Unit,
    useCameraBackground: Boolean,
    themeColor: Int,
    lightLevel: Float,
    selectedBallResource: Int
) {
    val outerPadding = 16.dp
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        LevelBackground(
            themeColor = themeColor,
            useCameraBackground = useCameraBackground
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding)
        ) {
            // Spacer na górze – symulacja miejsca dla status baru
            Spacer(modifier = Modifier.height(24.dp))

            // Wiersz z wynikiem i przyciskiem pauzy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimeMsToMMSS(score),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                )
                PauseButton(onClick = onPauseClick)
            }

            // Odstęp między wierszem a planszą
            Spacer(modifier = Modifier.height(16.dp))

            // Box zawierający planszę – responsywnie skalowaną
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(outerPadding)
            ) {
                // BoxWithConstraints pozwala poznać dostępny rozmiar
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val gridColumns = grid.firstOrNull()?.size ?: 0
                    val gridRows = grid.size

                    if (gridColumns > 0 && gridRows > 0) {
                        val availableWidth = maxWidth
                        val availableHeight = maxHeight
                        // Obliczamy nowy rozmiar komórki (dp)
                        val newCellSizeDp = minOf(availableWidth / gridColumns, availableHeight / gridRows)
                        val newCellSizePx = with(density) { newCellSizeDp.toPx() }

                        // Obliczamy współczynnik skalowania
                        val scaleFactor = newCellSizePx / originalCellSize

                        // Nowa średnica kulki (w pikselach)
                        val newBallDiameterPx = ball.radius * 2 * scaleFactor

                        // Cache'ujemy bitmapy dla przeszkód i kulki przy nowym rozmiarze
                        val scaledBitmaps = rememberScaledBitmaps(newCellSizePx, newBallDiameterPx)

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Rysowanie siatki i przeszkód
                            for (row in grid.indices) {
                                for (col in grid[row].indices) {
                                    val cell = grid[row][col]
                                    val topLeft = Offset(col * newCellSizePx, row * newCellSizePx)
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

                            // Rysowanie obwodu (linii) wokół planszy
                            val borderWidthPx = with(density) { 1.dp.toPx() }
                            drawRect(
                                color = Color.LightGray,
                                size = Size(width = gridColumns * newCellSizePx, height = gridRows * newCellSizePx),
                                style = Stroke(width = borderWidthPx)
                            )

                            // Rysowanie kulki – przeskalowane pozycje oraz promień
                            scaledBitmaps[selectedBallResource]?.let { ballBitmap ->
                                val scaledBallX = ball.x * scaleFactor
                                val scaledBallY = ball.y * scaleFactor
                                val scaledBallRadius = ball.radius * scaleFactor
                                drawIntoCanvas { canvas ->
                                    canvas.nativeCanvas.drawBitmap(
                                        ballBitmap.asAndroidBitmap(),
                                        scaledBallX - scaledBallRadius,
                                        scaledBallY - scaledBallRadius,
                                        null
                                    )
                                }
                            }
                        }
                    }
                }
            }
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
