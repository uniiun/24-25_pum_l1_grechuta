package com.example.ballin.model

enum class CellType {
    EMPTY,
    START,
    GOAL,
    OBSTACLE,
    OBSTACLE_RECTANGLE,
    OBSTACLE_CIRCLE
}

data class Cell(
    var type: CellType = CellType.EMPTY
)