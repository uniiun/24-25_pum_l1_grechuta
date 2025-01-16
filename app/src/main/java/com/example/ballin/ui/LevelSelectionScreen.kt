import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ballin.model.LevelManager

@Composable
fun LevelSelectionScreen(
    levelManager: LevelManager,
    onLevelSelected: (Int) -> Unit
) {
    val levels = remember { levelManager.getLevels() } // Pobranie poziomów

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            levels.forEach { level ->
                Button(
                    onClick = { onLevelSelected(level.id) }, // Przekazujemy ID poziomu
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Text(text = "Level ${level.id}")
                }
            }

        }
    }
}




