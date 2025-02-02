package com.example.ballin.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LevelManager(private val context: Context) {
    private val gson = Gson()
    private var levels: List<Level> = emptyList()

    var currentLevelId: Int = -1
        private set

    private var levelChangeListeners: MutableList<() -> Unit> = mutableListOf()

    fun loadLevelsFromJson(fileName: String) {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Level>>() {}.type
        levels = gson.fromJson(json, type)
    }

    fun getLevelById(id: Int): Level? {
        return levels.find { it.id == id }
    }

    fun setCurrentLevelById(id: Int) {
        if (levels.any { it.id == id }) {
            currentLevelId = id
            notifyLevelChange()
            Log.d("LevelManager", "Ustawiono bieżący poziom na ID: $id")
        } else {
            Log.e("LevelManager", "Nie znaleziono poziomu z ID: $id")
        }
    }

    fun getCurrentLevel(): Level? {
        return getLevelById(currentLevelId)
    }

    fun nextLevel() {
        val sortedLevels = levels.sortedBy { it.id }
        val next = sortedLevels.firstOrNull { it.id > currentLevelId }
        if (next != null) {
            currentLevelId = next.id
            notifyLevelChange()
            Log.d("LevelManager", "Przechodzę do następnego poziomu: ${next.id}")
        } else {
            currentLevelId = -1
            Log.d("LevelManager", "Nie ma kolejnego poziomu.")
        }
    }

    fun resetToFirstLevel() {
        val first = levels.minByOrNull { it.id }
        if (first != null) {
            currentLevelId = first.id
        }
    }

    fun getLevels(): List<Level> {
        return levels
    }

    fun addLevelChangeListener(listener: () -> Unit) {
        levelChangeListeners.add(listener)
    }

    fun removeLevelChangeListener(listener: () -> Unit) {
        levelChangeListeners.remove(listener)
    }

    private fun notifyLevelChange() {
        levelChangeListeners.forEach { it.invoke() }
    }
}
