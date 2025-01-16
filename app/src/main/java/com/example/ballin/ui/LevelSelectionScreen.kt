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
import com.example.ballin.ui.StyledButton

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
                StyledButton(
                    text = "Level ${level.id}",
                    onClick = { onLevelSelected(level.id) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

        }
    }
}




