package com.example.ballin.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StyledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD9165F), // Tło przycisku
            contentColor = Color(0xFFFFB200)  // Kolor tekstu
        ),
        shape = RoundedCornerShape(8.dp), // Lekkie zaokrąglenie rogów
        modifier = modifier
            .height(60.dp)
            .padding(8.dp)
            .shadow(
                elevation = 4.dp, // Intensywność cienia
                shape = RoundedCornerShape(8.dp), // Dopasowanie cienia do zaokrąglenia przycisku
                clip = false
            )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PauseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,

        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFFFB200)    // Kolor ikony i obwódki
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.Pause, // Test działającej ikony
            contentDescription = "Pause",
            tint = Color(0xFFFFB200),
            modifier = Modifier.size(48.dp)
        )

    }
}
@Preview(showBackground = true)
@Composable
fun PauseIconPreview() {
    PauseButton(onClick = {})
}






