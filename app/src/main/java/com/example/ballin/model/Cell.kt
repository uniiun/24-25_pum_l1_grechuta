package com.example.ballin.model

enum class CellType {
    EMPTY, OBSTACLE, START, GOAL
}

data class Cell(
    var type: CellType = CellType.EMPTY
)