package com.example.data.model

enum class AssistantState {
    IDLE,
    WAKE_WORD_LISTENING,
    ACTIVATED,
    LISTENING,
    PROCESSING,
    EXECUTING,
    SPEAKING,
    ERROR
}

enum class IntentType {
    TIME,
    DATE,
    OPEN_APP,
    MEDIA_PLAY,
    MEDIA_PAUSE,
    MEDIA_RESUME,
    MEDIA_NEXT,
    MEDIA_PREVIOUS,
    MEDIA_STOP,
    VOLUME_UP,
    VOLUME_DOWN,
    VOLUME_SET,
    VOLUME_MUTE,
    WEATHER_CURRENT,
    WEATHER_FORECAST,
    GENERAL_AI,
    UNKNOWN
}

data class CommandIntent(
    val type: IntentType,
    val rawText: String,
    val targetApp: String? = null,
    val volumePercent: Int? = null,
    val weatherDate: String? = null,
    val aiQuery: String? = null
)
