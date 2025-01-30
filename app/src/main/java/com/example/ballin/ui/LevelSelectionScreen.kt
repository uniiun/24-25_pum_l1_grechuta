import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ballin.model.LevelManager
import com.example.ballin.ui.StyledButton

@Composable
fun LevelSelectionScreen(
    levelManager: LevelManager,
    onLevelSelected: (Int) -> Unit
) {
    val levels = levelManager.getLevels()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            levels.forEach { level ->
                val bestTimeMs = getBestTimeForLevel(LocalContext.current, level.id)
                val bestTimeText = if (bestTimeMs == Long.MAX_VALUE) {
                    "--:--"
                } else {
                    formatTimeMsToMMSS(bestTimeMs)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StyledButton(
                        text = "Poziom ${level.id}",
                        onClick = { onLevelSelected(level.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Best: $bestTimeText",
                        color = Color(0xFFFFB200)
                    )
                }
            }
        }
    }
}

fun getBestTimeForLevel(context: Context, levelId: Int): Long {
    val sp = context.getSharedPreferences("GamePreferences", Context.MODE_PRIVATE)
    val key = "best_time_level_$levelId"
    return sp.getLong(key, Long.MAX_VALUE)
}

fun formatTimeMsToMMSS(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
