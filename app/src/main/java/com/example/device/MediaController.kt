package com.example.device

import android.content.Context
import android.media.AudioManager
import android.os.SystemClock
import android.view.KeyEvent

class MediaController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun sendMediaKeyEvent(keyCode: Int) {
        val eventTime = SystemClock.uptimeMillis()
        val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0)
        val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0)

        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    fun play(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY)
        return "Playing music."
    }

    fun pause(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE)
        return "Paused."
    }

    fun resume(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY)
        return "Resuming music."
    }

    fun playPauseToggle(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        return "Toggled playback."
    }

    fun next(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
        return "Playing next track."
    }

    fun previous(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        return "Playing previous track."
    }

    fun stop(): String {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_STOP)
        return "Music stopped."
    }

    fun isMusicActive(): Boolean {
        return audioManager.isMusicActive
    }
}
