package com.example.ballin.model

data class Obstacle(
    val x: Int,
    val y: Int,
    val type: ObstacleType
)

enum class ObstacleType {
    RECTANGLE,
    CIRCLE
}
