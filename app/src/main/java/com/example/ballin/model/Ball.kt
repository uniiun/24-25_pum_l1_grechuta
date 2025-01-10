package com.example.ballin.model

data class Ball(
    var x: Float,
    var y: Float,
    var dx: Float,
    var dy: Float,
    val radius: Float
) {
    fun updatePosition(width: Int, height: Int, dampingFactor: Float) {
        // Aktualizacja pozycji
        x += dx
        y += dy

        // Odbicie od ścian z tłumieniem
        if (x - radius < 0 || x + radius > width) {
            dx = -dx * dampingFactor // Zmniejsz prędkość po odbiciu
            x = if (x - radius < 0) radius else width - radius
        }

        if (y - radius < 0 || y + radius > height) {
            dy = -dy * dampingFactor // Zmniejsz prędkość po odbiciu
            y = if (y - radius < 0) radius else height - radius
        }
    }

fun resetPosition(width: Int, height: Int) {
        // Ustaw kulkę na środku ekranu
        x = width / 2f
        y = height / 2f
        dx = 0f
        dy = 0f
    }
}