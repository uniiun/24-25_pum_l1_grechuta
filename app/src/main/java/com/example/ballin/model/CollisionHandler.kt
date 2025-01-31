package com.example.ballin.model

import kotlin.math.pow
import kotlin.math.sqrt

class CollisionHandler(
    private val ball: Ball,
    private val grid: Array<Array<Cell>>,
    private val cellSize: Float
) {
    private val dampingFactor = 0.98f
    private val sectionSize = 2 // Zmniejszono sekcje do 2x2 komórki
    private val sections = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    private val cellRadius = cellSize / 2 // Prekomputowana wartość

    init {
        updateSections()
    }

    // Aktualizuj sekcje tylko raz przy inicjalizacji poziomu
    private fun updateSections() {
        sections.clear()
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col].type == CellType.OBSTACLE_RECTANGLE || grid[row][col].type == CellType.OBSTACLE_CIRCLE) {
                    val sectionKey = (col / sectionSize) * 100 + (row / sectionSize) // Mniejszy klucz
                    sections.getOrPut(sectionKey) { mutableListOf() }.add(col to row)
                }
            }
        }
    }

    fun checkCollision(onLevelComplete: () -> Unit) {
        // Szybkie sprawdzenie kolizji z celem
        grid.forEachIndexed { rowIdx, row ->
            row.forEachIndexed { colIdx, cell ->
                if (cell.type == CellType.GOAL && isInRange(colIdx * cellSize, rowIdx * cellSize)) {
                    if (ballCollidesWith(colIdx * cellSize, rowIdx * cellSize)) {
                        onLevelComplete()
                        return
                    }
                }
            }
        }

        // Kolizje z przeszkodami w pobliskich sekcjach
        val ballSectionX = (ball.x / (cellSize * sectionSize)).toInt()
        val ballSectionY = (ball.y / (cellSize * sectionSize)).toInt()

        for (dx in -1..1) {
            for (dy in -1..1) {
                val sectionKey = (ballSectionX + dx) * 100 + (ballSectionY + dy)
                sections[sectionKey]?.forEach { (col, row) ->
                    val x = col * cellSize
                    val y = row * cellSize

                    // Szybki test odległości przed szczegółową kolizją
                    if (!isInRange(x, y)) return@forEach

                    when (grid[row][col].type) {
                        CellType.OBSTACLE_RECTANGLE -> {
                            if (ballCollidesWith(x, y)) handleRectangleCollision(x, y)
                        }
                        CellType.OBSTACLE_CIRCLE -> {
                            val centerX = x + cellRadius
                            val centerY = y + cellRadius
                            if (ballCollidesWithCircle(centerX, centerY)) handleCircleCollision(centerX, centerY)
                        }
                        else -> {}
                    }
                }
            }
        }
        checkWallCollision()
    }

    // Sprawdź czy przeszkoda jest w zasięgu kulki (szybki test)
    private fun isInRange(x: Float, y: Float): Boolean {
        val maxDistance = ball.radius + cellSize * 1.5f // Zapas 1.5 komórki
        return (ball.x - x).pow(2) + (ball.y - y).pow(2) < maxDistance.pow(2)
    }

    private fun checkWallCollision(): Boolean {
        val mapWidth = grid[0].size * cellSize
        val mapHeight = grid.size * cellSize
        val offset = ball.radius * 1.1f

        var wallCollision = false

        if (ball.x - ball.radius <= 0) {
            ball.dx = -ball.dx
            ball.x = ball.radius + offset
            wallCollision = true
        }

        if (ball.x + ball.radius >= mapWidth) {
            ball.dx = -ball.dx
            ball.x = mapWidth - ball.radius - offset
            wallCollision = true
        }

        if (ball.y - ball.radius <= 0) {
            ball.dy = -ball.dy
            ball.y = ball.radius + offset
            wallCollision = true
        }

        if (ball.y + ball.radius >= mapHeight) {
            ball.dy = -ball.dy
            ball.y = mapHeight - ball.radius - offset
            wallCollision = true
        }

        return wallCollision
    }

    private fun ballCollidesWith(x: Float, y: Float): Boolean {
        return ball.x + ball.radius > x &&
                ball.x - ball.radius < x + cellSize &&
                ball.y + ball.radius > y &&
                ball.y - ball.radius < y + cellSize
    }

    private fun ballCollidesWithCircle(cx: Float, cy: Float): Boolean {
        val dx = ball.x - cx
        val dy = ball.y - cy
        return dx * dx + dy * dy < (ball.radius + cellRadius).pow(2)
    }

    private fun handleRectangleCollision(rectX: Float, rectY: Float) {
        if (ball.x < rectX || ball.x > rectX + cellSize) ball.dx = -ball.dx
        if (ball.y < rectY || ball.y > rectY + cellSize) ball.dy = -ball.dy
        ball.dx *= dampingFactor
        ball.dy *= dampingFactor
    }

    private fun handleCircleCollision(circleX: Float, circleY: Float) {
        val dx = ball.x - circleX
        val dy = ball.y - circleY
        val distance = sqrt(dx * dx + dy * dy)

        if (distance != 0f) {
            val nx = dx / distance
            val ny = dy / distance
            val dotProduct = ball.dx * nx + ball.dy * ny
            ball.dx -= 2 * dotProduct * nx
            ball.dy -= 2 * dotProduct * ny
        }
        ball.dx *= dampingFactor
        ball.dy *= dampingFactor
    }
}