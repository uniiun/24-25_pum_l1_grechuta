package com.example.ballin.model

data class Ball(
    var x: Float,
    var y: Float,
    var dx: Float,
    var dy: Float,
    val radius: Float
) {
    fun updatePosition(gridWidth: Int, gridHeight: Int, cellSize: Float, dampingFactor: Float) {
        // Aktualizacja pozycji kulki
        x += dx
        y += dy

        // Odbicie od lewej i prawej krawędzi siatki
        if (x - radius < 0) {
            x = radius
            dx = -dx * dampingFactor
        } else if (x + radius > gridWidth * cellSize) {
            x = gridWidth * cellSize - radius
            dx = -dx * dampingFactor
        }

        // Odbicie od górnej i dolnej krawędzi siatki
        if (y - radius < 0) {
            y = radius
            dy = -dy * dampingFactor
        } else if (y + radius > gridHeight * cellSize) {
            y = gridHeight * cellSize - radius
            dy = -dy * dampingFactor
        }
    }
}
