package com.example.ballin.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class LevelManager(private val context: Context) {
    private val gson = Gson()
    private var levels: List<Level> = emptyList()
    var currentLevelIndex: Int = 0

    fun loadLevelsFromJson(fileName: String) {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Level>>() {}.type
        levels = gson.fromJson(json, type)
    }

    fun getCurrentLevel(): Level? {
        return if (currentLevelIndex in levels.indices) levels[currentLevelIndex] else null
    }

    fun nextLevel() {
        if (currentLevelIndex < levels.size - 1) {
            currentLevelIndex++
        }
    }

    fun resetToFirstLevel() {
        currentLevelIndex = 0
    }
}
