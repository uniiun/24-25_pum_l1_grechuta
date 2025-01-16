package com.example.ballin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            .height(60.dp)  // Ustalona wysokość
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

