package com.example.ballin.model

import android.graphics.drawable.Drawable

class Ball(
    var x: Float,
    var y: Float,
    var dx: Float,
    var dy: Float,
    var radius: Float,
    var drawable: Drawable? = null
) {
    fun updatePosition(gridWidth: Int, gridHeight: Int, cellSize: Float) {
        x += dx
        y += dy

        if (x - radius < 0) {
            x = radius
            dx = -dx
        } else if (x + radius > gridWidth * cellSize) {
            x = gridWidth * cellSize - radius
            dx = -dx
        }

        if (y - radius < 0) {
            y = radius
            dy = -dy
        } else if (y + radius > gridHeight * cellSize) {
            y = gridHeight * cellSize - radius
            dy = -dy
        }
    }
}
