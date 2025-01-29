package com.example.ballin.model

import android.content.Context
import android.os.Build
import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

class CollisionHandler(
    private val context: Context,
    private val ball: Ball,
    private val grid: Array<Array<Cell>>,
    private val cellSize: Float
) {
    private val dampingFactor = 0.98f

    fun checkCollision(onLevelComplete: () -> Unit) {
        checkWallCollision()

        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val cell = grid[row][col]
                val x = col * cellSize
                val y = row * cellSize

                when (cell.type) {
                    CellType.GOAL -> if (ballCollidesWith(x, y)) onLevelComplete()
                    CellType.OBSTACLE_RECTANGLE -> if (ballCollidesWith(x, y)) {
                        handleRectangleCollision(x, y)
                    }
                    CellType.OBSTACLE_CIRCLE -> {
                        val circleCenterX = x + cellSize / 2
                        val circleCenterY = y + cellSize / 2
                        if (ballCollidesWithCircle(circleCenterX, circleCenterY)) {
                            handleCircleCollision(circleCenterX, circleCenterY)

                        }
                    }
                    CellType.START, CellType.EMPTY -> {}
                    else -> Log.w("CollisionHandler", "Nieobsługiwany typ komórki: ${cell.type}")
                }
            }
        }
    }

    private fun checkWallCollision() {
        val mapWidth = grid[0].size * cellSize
        val mapHeight = grid.size * cellSize
        val offset = ball.radius * 1.1f

        var wallCollision = false

        if (ball.x - ball.radius <= 0) {
            Log.d("VIBRATION", "Kolizja ze ścianą (lewa)")
            ball.dx = -ball.dx
            ball.x = ball.radius + offset
            wallCollision = true
        }

        if (ball.x + ball.radius >= mapWidth) {
            Log.d("VIBRATION", "Kolizja ze ścianą (prawa)")
            ball.dx = -ball.dx
            ball.x = mapWidth - ball.radius - offset
            wallCollision = true
        }

        if (ball.y - ball.radius <= 0) {
            Log.d("VIBRATION", "Kolizja ze ścianą (góra)")
            ball.dy = -ball.dy
            ball.y = ball.radius + offset
            wallCollision = true
        }

        if (ball.y + ball.radius >= mapHeight) {
            Log.d("VIBRATION", "Kolizja ze ścianą (dół)")
            ball.dy = -ball.dy
            ball.y = mapHeight - ball.radius - offset
            wallCollision = true
        }

        if (wallCollision) {

        }
    }

    private fun ballCollidesWith(x: Float, y: Float): Boolean {
        return ball.x + ball.radius > x && ball.x - ball.radius < x + cellSize &&
                ball.y + ball.radius > y && ball.y - ball.radius < y + cellSize
    }

    private fun ballCollidesWithCircle(cx: Float, cy: Float): Boolean {
        val dx = ball.x - cx
        val dy = ball.y - cy
        return sqrt(dx.pow(2) + dy.pow(2)) < ball.radius + cellSize / 2
    }

    private fun handleRectangleCollision(rectX: Float, rectY: Float) {
        if (ball.x < rectX || ball.x > rectX + cellSize) {
            ball.dx = -ball.dx
        }
        if (ball.y < rectY || ball.y > rectY + cellSize) {
            ball.dy = -ball.dy
        }
        ball.dx *= dampingFactor
        ball.dy *= dampingFactor
    }

    private fun handleCircleCollision(circleX: Float, circleY: Float) {
        val dx = ball.x - circleX
        val dy = ball.y - circleY
        val distance = sqrt(dx.pow(2) + dy.pow(2))

        if (distance != 0.0f) {
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
