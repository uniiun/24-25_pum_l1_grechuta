package com.example.ballin.model

import kotlin.math.pow
import kotlin.math.sqrt

class CollisionHandler(
    private val grid: Array<Array<Cell>>,
    private val cellSize: Float,
    private val dampingFactor: Float
) {
    fun checkCollision(ball: Ball, onLevelComplete: () -> Unit) {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val cell = grid[row][col]
                val x = col * cellSize
                val y = row * cellSize

                when (cell.type) {
                    CellType.GOAL -> {
                        if (isCollidingWithCell(ball, x, y)) {
                            onLevelComplete()
                        }
                    }
                    CellType.OBSTACLE_RECTANGLE -> {
                        if (isCollidingWithCell(ball, x, y)) {
                            handleRectangleCollision(ball, x, y)
                        }
                    }
                    CellType.OBSTACLE_CIRCLE -> {
                        val circleCenterX = x + cellSize / 2
                        val circleCenterY = y + cellSize / 2
                        if (isCollidingWithCircle(ball, circleCenterX, circleCenterY)) {
                            handleCircleCollision(ball, circleCenterX, circleCenterY)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun isCollidingWithCell(ball: Ball, x: Float, y: Float): Boolean {
        return ball.x + ball.radius > x &&
                ball.x - ball.radius < x + cellSize &&
                ball.y + ball.radius > y &&
                ball.y - ball.radius < y + cellSize
    }

    private fun isCollidingWithCircle(ball: Ball, circleX: Float, circleY: Float): Boolean {
        val distance = sqrt((ball.x - circleX).pow(2) + (ball.y - circleY).pow(2))
        return distance < ball.radius + cellSize / 2
    }

    private fun handleRectangleCollision(ball: Ball, rectX: Float, rectY: Float) {
        if (ball.x < rectX || ball.x > rectX + cellSize) {
            ball.dx = -ball.dx
        }
        if (ball.y < rectY || ball.y > rectY + cellSize) {
            ball.dy = -ball.dy
        }
        applyDamping(ball)
    }

    private fun handleCircleCollision(ball: Ball, circleX: Float, circleY: Float) {
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
        applyDamping(ball)
    }

    private fun applyDamping(ball: Ball) {
        ball.dx *= dampingFactor
        ball.dy *= dampingFactor
    }
}
