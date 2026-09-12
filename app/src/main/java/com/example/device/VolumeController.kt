package com.example.device

import android.content.Context
import android.media.AudioManager
import kotlin.math.roundToInt

class VolumeController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun increaseVolume(): String {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        val currentPercent = getCurrentVolumePercent()
        return "Volume increased to $currentPercent percent."
    }

    fun decreaseVolume(): String {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        val currentPercent = getCurrentVolumePercent()
        return "Volume decreased to $currentPercent percent."
    }

    fun setVolumePercent(percent: Int): String {
        val clampedPercent = percent.coerceIn(0, 100)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetIndex = ((clampedPercent / 100.0f) * maxVolume).roundToInt()

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetIndex, AudioManager.FLAG_SHOW_UI)
        return "Volume set to $clampedPercent percent."
    }

    fun mute(): String {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
        return "Volume muted."
    }

    fun getCurrentVolumePercent(): Int {
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (max == 0) return 0
        return ((current.toFloat() / max) * 100).roundToInt()
    }
}
