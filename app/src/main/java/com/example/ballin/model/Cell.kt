package com.example.ballin.model

enum class CellType {
    EMPTY,
    START,
    GOAL,
    OBSTACLE,
    OBSTACLE_RECTANGLE, // Nowy typ - prostokątna przeszkoda
    OBSTACLE_CIRCLE // Nowy typ - okrągła przeszkoda
}

data class Cell(
    var type: CellType = CellType.EMPTY
)