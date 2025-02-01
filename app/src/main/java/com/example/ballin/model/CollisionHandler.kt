package com.example.ballin.model

import kotlin.math.pow
import kotlin.math.sqrt

class CollisionHandler(
    private val ball: Ball,
    private val grid: Array<Array<Cell>>,
    private val cellSize: Float
) {
    private val dampingFactor = 0.8f
    private val wallRestitution = 0.5f
    private val sectionSize = 2
    private val sections = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    private val cellRadius = cellSize / 2

    init {
        updateSections()
    }

    private fun updateSections() {
        sections.clear()
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col].type == CellType.OBSTACLE_RECTANGLE || grid[row][col].type == CellType.OBSTACLE_CIRCLE) {
                    val sectionKey = (col / sectionSize) * 100 + (row / sectionSize)
                    sections.getOrPut(sectionKey) { mutableListOf() }.add(col to row)
                }
            }
        }
    }

    fun checkCollision(onLevelComplete: () -> Unit) {
        grid.forEachIndexed { rowIdx, row ->
            row.forEachIndexed { colIdx, cell ->
                if (cell.type == CellType.GOAL && ballCollidesWith(colIdx * cellSize, rowIdx * cellSize)) {
                    onLevelComplete()
                    return
                }
            }
        }

        val ballSectionX = (ball.x / (cellSize * sectionSize)).toInt()
        val ballSectionY = (ball.y / (cellSize * sectionSize)).toInt()

        for (dx in -1..1) {
            for (dy in -1..1) {
                val sectionKey = (ballSectionX + dx) * 100 + (ballSectionY + dy)
                sections[sectionKey]?.forEach { (col, row) ->
                    val x = col * cellSize
                    val y = row * cellSize
                    if (!isInRange(x, y)) return@forEach

                    when (grid[row][col].type) {
                        CellType.OBSTACLE_RECTANGLE -> {
                            if (ballCollidesWith(x, y)) {
                                handleRectangleCollision(x, y)
                            }
                        }
                        CellType.OBSTACLE_CIRCLE -> {
                            val centerX = x + cellRadius
                            val centerY = y + cellRadius
                            if (ballCollidesWithCircle(centerX, centerY)) {
                                handleCircleCollision(centerX, centerY)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
        checkWallCollision()
    }

    private fun isInRange(x: Float, y: Float): Boolean {
        val maxDistance = ball.radius + cellSize * 1.5f
        return (ball.x - x).pow(2) + (ball.y - y).pow(2) < maxDistance.pow(2)
    }

    private fun checkWallCollision(): Boolean {
        val mapWidth = grid[0].size * cellSize
        val mapHeight = grid.size * cellSize
        var collided = false

        if (ball.x - ball.radius < 0) {
            ball.x = ball.radius
            ball.dx = -ball.dx * wallRestitution
            collided = true
        } else if (ball.x + ball.radius > mapWidth) {
            ball.x = mapWidth - ball.radius
            ball.dx = -ball.dx * wallRestitution
            collided = true
        }

        if (ball.y - ball.radius < 0) {
            ball.y = ball.radius
            ball.dy = -ball.dy * wallRestitution
            collided = true
        } else if (ball.y + ball.radius > mapHeight) {
            ball.y = mapHeight - ball.radius
            ball.dy = -ball.dy * wallRestitution
            collided = true
        }
        return collided
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
        val closestX = ball.x.coerceIn(rectX, rectX + cellSize)
        val closestY = ball.y.coerceIn(rectY, rectY + cellSize)
        val diffX = ball.x - closestX
        val diffY = ball.y - closestY
        val distance = sqrt(diffX * diffX + diffY * diffY)
        if (distance == 0f) {
            ball.x += 1f
            return
        }
        val penetration = ball.radius - distance
        if (penetration > 0f) {
            val nx = diffX / distance
            val ny = diffY / distance
            ball.x += nx * penetration
            ball.y += ny * penetration
            val dot = ball.dx * nx + ball.dy * ny
            ball.dx -= 2 * dot * nx
            ball.dy -= 2 * dot * ny
            ball.dx *= dampingFactor
            ball.dy *= dampingFactor
        }
    }

    private fun handleCircleCollision(circleX: Float, circleY: Float) {
        val dx = ball.x - circleX
        val dy = ball.y - circleY
        val distance = sqrt(dx * dx + dy * dy)
        if (distance == 0f) {
            ball.x += 1f
            return
        }
        val penetration = (ball.radius + cellRadius) - distance
        if (penetration > 0f) {
            val nx = dx / distance
            val ny = dy / distance
            ball.x += nx * penetration
            ball.y += ny * penetration
            val dotProduct = ball.dx * nx + ball.dy * ny
            ball.dx -= 2 * dotProduct * nx
            ball.dy -= 2 * dotProduct * ny
            ball.dx *= dampingFactor
            ball.dy *= dampingFactor
        }
    }
}
