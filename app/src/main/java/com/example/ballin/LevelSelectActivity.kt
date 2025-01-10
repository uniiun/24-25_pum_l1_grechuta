package com.example.ballin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ballin.ui.theme.BallinTheme

class LevelSelectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BallinTheme {
                LevelSelectScreen(
                    onLevel1Click = { /* Obsługa wyboru poziomu 1 */ },
                    onLevel2Click = { /* Obsługa wyboru poziomu 2 */ }
                )
            }
        }
    }
}

@Composable
fun LevelSelectScreen(
    onLevel1Click: () -> Unit,
    onLevel2Click: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onLevel1Click, modifier = Modifier.padding(bottom = 16.dp)) {
            Text(text = "Level 1")
        }
        Button(onClick = onLevel2Click) {
            Text(text = "Level 2")
        }
    }
}
