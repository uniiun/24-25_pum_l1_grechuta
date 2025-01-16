package com.example.ballin.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LevelManager(private val context: Context) {
    private val gson = Gson()
    private var levels: List<Level> = emptyList()
    var currentLevelIndex: Int = 0
        set(value) {
            field = value
            notifyLevelChange() // Powiadomienie o zmianie poziomu
        }

    private var levelChangeListeners: MutableList<() -> Unit> = mutableListOf()

    fun loadLevelsFromJson(fileName: String) {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Level>>() {}.type
        levels = gson.fromJson(json, type)
    }

    fun getLevelById(id: Int): Level? {
        return levels.find { it.id == id }
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

    fun getLevels(): List<Level> {
        return levels
    }

    // Dodanie słuchacza zmian poziomu
    fun addLevelChangeListener(listener: () -> Unit) {
        levelChangeListeners.add(listener)
    }

    // Usunięcie słuchacza zmian poziomu
    fun removeLevelChangeListener(listener: () -> Unit) {
        levelChangeListeners.remove(listener)
    }

    // Powiadamianie wszystkich słuchaczy
    private fun notifyLevelChange() {
        levelChangeListeners.forEach { it.invoke() }
    }
}
