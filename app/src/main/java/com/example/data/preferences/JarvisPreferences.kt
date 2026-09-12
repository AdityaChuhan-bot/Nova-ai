package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

enum class OperationMode {
    PUSH_TO_TALK,
    VOICE_SESSION,
    ALWAYS_LISTENING
}

enum class BatteryMode {
    BALANCED,
    LOW_POWER,
    ALWAYS_ON
}

data class JarvisSettings(
    val wakeWord: String = "Hey Jarvis",
    val operationMode: OperationMode = OperationMode.PUSH_TO_TALK,
    val sessionTimeoutSeconds: Int = 15,
    val speechSpeed: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val customGeminiApiKey: String = "",
    val enableAiFallback: Boolean = true,
    val defaultMusicApp: String = "SimpMusic",
    val defaultMusicPackage: String = "com.maxrave.simpmusic",
    val weatherCity: String = "New York",
    val weatherUnit: String = "Celsius", // "Celsius" or "Fahrenheit"
    val keepScreenOn: Boolean = true,
    val startOnBoot: Boolean = false,
    val batteryMode: BatteryMode = BatteryMode.BALANCED
)

class JarvisPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jarvis_mini_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): JarvisSettings {
        val modeStr = prefs.getString(KEY_OPERATION_MODE, OperationMode.PUSH_TO_TALK.name) ?: OperationMode.PUSH_TO_TALK.name
        val mode = try { OperationMode.valueOf(modeStr) } catch (e: Exception) { OperationMode.PUSH_TO_TALK }

        val batteryStr = prefs.getString(KEY_BATTERY_MODE, BatteryMode.BALANCED.name) ?: BatteryMode.BALANCED.name
        val battery = try { BatteryMode.valueOf(batteryStr) } catch (e: Exception) { BatteryMode.BALANCED }

        return JarvisSettings(
            wakeWord = prefs.getString(KEY_WAKE_WORD, "Hey Jarvis") ?: "Hey Jarvis",
            operationMode = mode,
            sessionTimeoutSeconds = prefs.getInt(KEY_SESSION_TIMEOUT, 15),
            speechSpeed = prefs.getFloat(KEY_SPEECH_SPEED, 1.0f),
            speechPitch = prefs.getFloat(KEY_SPEECH_PITCH, 1.0f),
            customGeminiApiKey = prefs.getString(KEY_GEMINI_KEY, "") ?: "",
            enableAiFallback = prefs.getBoolean(KEY_AI_FALLBACK, true),
            defaultMusicApp = prefs.getString(KEY_MUSIC_APP, "SimpMusic") ?: "SimpMusic",
            defaultMusicPackage = prefs.getString(KEY_MUSIC_PACKAGE, "com.maxrave.simpmusic") ?: "com.maxrave.simpmusic",
            weatherCity = prefs.getString(KEY_WEATHER_CITY, "New York") ?: "New York",
            weatherUnit = prefs.getString(KEY_WEATHER_UNIT, "Celsius") ?: "Celsius",
            keepScreenOn = prefs.getBoolean(KEY_KEEP_SCREEN_ON, true),
            startOnBoot = prefs.getBoolean(KEY_START_ON_BOOT, false),
            batteryMode = battery
        )
    }

    fun saveSettings(settings: JarvisSettings) {
        prefs.edit()
            .putString(KEY_WAKE_WORD, settings.wakeWord)
            .putString(KEY_OPERATION_MODE, settings.operationMode.name)
            .putInt(KEY_SESSION_TIMEOUT, settings.sessionTimeoutSeconds)
            .putFloat(KEY_SPEECH_SPEED, settings.speechSpeed)
            .putFloat(KEY_SPEECH_PITCH, settings.speechPitch)
            .putString(KEY_GEMINI_KEY, settings.customGeminiApiKey)
            .putBoolean(KEY_AI_FALLBACK, settings.enableAiFallback)
            .putString(KEY_MUSIC_APP, settings.defaultMusicApp)
            .putString(KEY_MUSIC_PACKAGE, settings.defaultMusicPackage)
            .putString(KEY_WEATHER_CITY, settings.weatherCity)
            .putString(KEY_WEATHER_UNIT, settings.weatherUnit)
            .putBoolean(KEY_KEEP_SCREEN_ON, settings.keepScreenOn)
            .putBoolean(KEY_START_ON_BOOT, settings.startOnBoot)
            .putString(KEY_BATTERY_MODE, settings.batteryMode.name)
            .apply()
    }

    companion object {
        private const val KEY_WAKE_WORD = "wake_word"
        private const val KEY_OPERATION_MODE = "operation_mode"
        private const val KEY_SESSION_TIMEOUT = "session_timeout"
        private const val KEY_SPEECH_SPEED = "speech_speed"
        private const val KEY_SPEECH_PITCH = "speech_pitch"
        private const val KEY_GEMINI_KEY = "gemini_key"
        private const val KEY_AI_FALLBACK = "ai_fallback"
        private const val KEY_MUSIC_APP = "music_app"
        private const val KEY_MUSIC_PACKAGE = "music_package"
        private const val KEY_WEATHER_CITY = "weather_city"
        private const val KEY_WEATHER_UNIT = "weather_unit"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_START_ON_BOOT = "start_on_boot"
        private const val KEY_BATTERY_MODE = "battery_mode"
    }
}
