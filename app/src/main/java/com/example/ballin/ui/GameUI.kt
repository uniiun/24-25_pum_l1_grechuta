package com.example.ballin.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
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
fun rememberScaledBitmaps(
    cellSize: Float,
    ballDiameter: Float,
    backgroundWidthPx: Float? = null
): Map<Int, ImageBitmap> {
    val context = LocalContext.current
    return remember(cellSize, ballDiameter, backgroundWidthPx) {
        val map = mutableMapOf<Int, ImageBitmap>()
        map[R.drawable.rock] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.rock)!!
        ).scaleToSize(cellSize)
        map[R.drawable.bush] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.bush)!!
        ).scaleToSize(cellSize)
        map[R.drawable.start] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.start)!!
        ).scaleToSize(cellSize)
        map[R.drawable.goal] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.goal)!!
        ).scaleToSize(cellSize)
        map[R.drawable.benson] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.benson)!!
        ).scaleToSize(ballDiameter)
        map[R.drawable.bluson] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.bluson)!!
        ).scaleToSize(ballDiameter)
        map[R.drawable.greenson] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.greenson)!!
        ).scaleToSize(ballDiameter)
        map[R.drawable.roson] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.roson)!!
        ).scaleToSize(ballDiameter)
        map[R.drawable.yellson] = drawableToImageBitmap(
            ContextCompat.getDrawable(context, R.drawable.yellson)!!
        ).scaleToSize(ballDiameter)
        if (backgroundWidthPx != null) {
            val bgDrawable = ContextCompat.getDrawable(context, R.drawable.background)!!
            map[R.drawable.background] = drawableToImageBitmap(bgDrawable).scaleToWidth(backgroundWidthPx)
        }
        map
    }
}

fun ImageBitmap.scaleToWidth(targetWidth: Float): ImageBitmap {
    val bitmap = this.asAndroidBitmap()
    val ratio = targetWidth / bitmap.width
    val targetHeight = (bitmap.height * ratio).toInt()
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth.toInt(), targetHeight, true)
    return scaledBitmap.asImageBitmap()
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
    // Użyjemy jednego dużego Boxa, w którym ułożymy:
    // 1) Tło (LevelBackground)
    // 2) Cały układ ekranu (Column)
    // 3) Canvas przyciemniający obszar poza siatką
    Box(modifier = Modifier.fillMaxSize()) {
        // 1) Tło
        LevelBackground(themeColor = themeColor, useCameraBackground = useCameraBackground)

        // 2) Cały układ ekranu, w tym plansza
        val gridRectPx = remember { mutableStateOf<Rect?>(null) } // współrzędne siatki

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Pasek (24.dp) dla status bara
            Spacer(modifier = Modifier.height(24.dp))

            // Pasek z wynikiem i przyciskiem pauzy
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

            Spacer(modifier = Modifier.height(16.dp))

            // Box na planszę
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Rysujemy siatkę w BoxWithConstraints
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        // Odczytujemy współrzędne i rozmiar BoxWithConstraints (czyli obszaru siatki)
                        .onGloballyPositioned { coords ->
                            val windowBounds = coords.boundsInWindow()
                            // boundsInWindow() zwraca Rect w pikselach ekranu
                            gridRectPx.value = Rect(
                                left = windowBounds.left,
                                top = windowBounds.top,
                                right = windowBounds.right,
                                bottom = windowBounds.bottom
                            )
                        }
                ) {
                    val density = LocalDensity.current

                    val gridColumns = grid.firstOrNull()?.size ?: 0
                    val gridRows = grid.size

                    if (gridColumns > 0 && gridRows > 0) {
                        // Obliczamy nowy rozmiar komórki w dp
                        val newCellSizeDp = minOf(
                            maxWidth / gridColumns,
                            maxHeight / gridRows
                        )
                        val newCellSizePx = with(density) { newCellSizeDp.toPx() }

                        // Współczynnik skalowania = nowy_rozmiar / oryginalny_rozmiar
                        val scaleFactor = newCellSizePx / originalCellSize
                        val newBallDiameterPx = ball.radius * 2 * scaleFactor

                        // Cache'ujemy bitmapy przeszkód i kulki
                        val scaledBitmaps = rememberScaledBitmaps(newCellSizePx, newBallDiameterPx)

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasSize = size
                            // Wielkość siatki w pikselach
                            val gridWidthPx = gridColumns * newCellSizePx
                            val gridHeightPx = gridRows * newCellSizePx

                            // Wyliczamy offset, by wycentrować siatkę w danym BoxWithConstraints
                            val offsetX = (canvasSize.width - gridWidthPx) / 2f
                            val offsetY = (canvasSize.height - gridHeightPx) / 2f

                            // Rysujemy siatkę
                            for (row in grid.indices) {
                                for (col in grid[row].indices) {
                                    val cell = grid[row][col]
                                    val topLeft = Offset(offsetX + col * newCellSizePx, offsetY + row * newCellSizePx)
                                    when (cell.type) {
                                        CellType.OBSTACLE_RECTANGLE ->
                                            scaledBitmaps[R.drawable.rock]?.let { drawImage(it, topLeft) }
                                        CellType.OBSTACLE_CIRCLE ->
                                            scaledBitmaps[R.drawable.bush]?.let { drawImage(it, topLeft) }
                                        CellType.START ->
                                            scaledBitmaps[R.drawable.start]?.let { drawImage(it, topLeft) }
                                        CellType.GOAL ->
                                            scaledBitmaps[R.drawable.goal]?.let { drawImage(it, topLeft) }
                                        else -> {}
                                    }
                                }
                            }

                            // Rysujemy kulkę
                            scaledBitmaps[selectedBallResource]?.let { ballBitmap ->
                                val scaledBallX = offsetX + ball.x * scaleFactor
                                val scaledBallY = offsetY + ball.y * scaleFactor
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

        // 3) Canvas, który rysuje przyciemnienie na całym ekranie,
        // wycinając obszar siatki
        val overlayAlpha = 0.5f
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridRect = gridRectPx.value
            if (gridRect != null) {
                // Rozmiar całego ekranu (Canvas)
                val screenSize = size

                // Zamieniamy Rect z px w "androidx.compose.ui.geometry.Rect"
                // Pamiętaj, że 'gridRect' jest w pikselach ekranu, a Canvas też operuje w pikselach,
                // więc możemy użyć współrzędnych bez dalszej konwersji.
                clipRect(
                    left = gridRect.left,
                    top = gridRect.top,
                    right = gridRect.right,
                    bottom = gridRect.bottom,
                    clipOp = ClipOp.Difference
                ) {
                    // Wypełniamy cały obszar Canvas ciemnym prostokątem
                    drawRect(
                        color = Color.Black.copy(alpha = overlayAlpha),
                        size = screenSize
                    )
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
            BackgroundImage(alpha = 0.5f)
        } else {
            BackgroundImage(alpha = 1f)
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
fun BackgroundImage(alpha: Float) {
    val context = LocalContext.current
    val backgroundImage = remember {
        drawableToImageBitmap(ContextCompat.getDrawable(context, R.drawable.background)!!)
    }
    Image(
        bitmap = backgroundImage,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        alpha = alpha
    )
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
