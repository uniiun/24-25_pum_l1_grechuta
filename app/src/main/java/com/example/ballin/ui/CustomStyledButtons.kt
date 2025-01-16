import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomStyledButtons(
    onStartClick: () -> Unit,
    onSelectColorClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Przyciski START
        Button(
            onClick = { onStartClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9165F)), // @color/secondary
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(200.dp)
                .height(60.dp),
            contentPadding = PaddingValues()
        ) {
            Text(
                text = "START",
                color = Color(0xFFFFB200), // @color/highlight
                fontWeight = FontWeight.Bold
            )
        }

        // Przyciski WYBIERZ KOLOR
        Button(
            onClick = { onSelectColorClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9165F)), // @color/secondary
            modifier = Modifier
                .padding(bottom = 268.dp)
                .width(200.dp)
                .height(60.dp),
            contentPadding = PaddingValues()
        ) {
            Text(
                text = "WYBIERZ KOLOR",
                color = Color(0xFFFFB200), // @color/highlight
                fontWeight = FontWeight.Bold
            )
        }
    }
}
