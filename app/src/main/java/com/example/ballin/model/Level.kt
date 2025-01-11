package com.example.ballin.model

data class Position(
    val x: Int,
    val y: Int
)

data class Level(
    val width: Int,
    val height: Int,
    val startPosition: Position,
    val goalPosition: Position,
    val obstacles: List<Obstacle>
)

data class Obstacle(
    val x: Int,
    val y: Int
)
