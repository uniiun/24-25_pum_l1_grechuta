package com.example.ballin.model

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.SoundPool

class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(5).build()
    private val soundMap = mutableMapOf<String, Int>()
    private val assetManager = context.assets

    init {
        loadSound("pop.wav", "pop")
        loadSound("bush.wav", "bush")
    }

    private fun loadSound(filename: String, key: String) {
        val afd: AssetFileDescriptor = assetManager.openFd(filename)
        val soundId = soundPool.load(afd, 1)
        soundMap[key] = soundId
    }

    fun playSound(key: String) {
        soundMap[key]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
