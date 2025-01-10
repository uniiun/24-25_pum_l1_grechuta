package com.example.ballin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ballin.model.Cell
import com.example.ballin.model.CellType

@Composable
fun GameGrid(grid: Array<Array<Cell>>, cellSize: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Rysowanie siatki
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val cell = grid[row][col]
                val x = col * cellSize
                val y = row * cellSize

                val color = when (cell.type) {
                    CellType.EMPTY -> Color.LightGray
                    CellType.OBSTACLE -> Color.DarkGray
                    CellType.START -> Color.Green
                    CellType.GOAL -> Color.Red
                }

                drawRect(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                )
            }
        }
    }
}
