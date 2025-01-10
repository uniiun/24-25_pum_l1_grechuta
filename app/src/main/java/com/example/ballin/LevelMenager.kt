package com.example.ballin

import android.content.Context

class LevelManager(private val context: Context) {

    private var currentLevel: Int = 1

    fun loadLevel(level: Int) {
        currentLevel = level
        // Logika ładowania poziomu
    }

    fun getCurrentLevel(): Int {
        return currentLevel
    }

    fun nextLevel() {
        currentLevel++
        // Logika ładowania następnego poziomu
    }
}
