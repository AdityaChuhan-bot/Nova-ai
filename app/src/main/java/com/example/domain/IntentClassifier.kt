package com.example.domain

import com.example.data.model.CommandIntent
import com.example.data.model.IntentType

class IntentClassifier {

    fun classify(rawInput: String): CommandIntent {
        val text = rawInput.trim().lowercase()

        // Strip wake word prefix if recognized in speech
        val cleanedText = text
            .removePrefix("hey jarvis")
            .removePrefix("ok jarvis")
            .removePrefix("jarvis")
            .removePrefix("computer")
            .trim()
            .removePrefix(",")
            .trim()

        val normalized = if (cleanedText.isEmpty()) text else cleanedText

        // 1. Time Intent
        if (normalized.matches(Regex(".*(what time is it|tell me the time|what's the time|current time|what time).*"))) {
            return CommandIntent(IntentType.TIME, rawInput)
        }

        // 2. Date Intent
        if (normalized.matches(Regex(".*(what is today's date|what day is it|tell me today's date|what's the date|what is the date|today's date).*"))) {
            return CommandIntent(IntentType.DATE, rawInput)
        }

        // 3. Media Controls
        if (normalized == "pause the music" || normalized == "pause music" || normalized == "pause" || normalized == "pause playback") {
            return CommandIntent(IntentType.MEDIA_PAUSE, rawInput)
        }
        if (normalized == "play music" || normalized == "resume music" || normalized == "resume" || normalized == "play" || normalized == "continue music") {
            return CommandIntent(IntentType.MEDIA_PLAY, rawInput)
        }
        if (normalized == "next song" || normalized == "next track" || normalized == "skip song" || normalized == "next" || normalized == "skip") {
            return CommandIntent(IntentType.MEDIA_NEXT, rawInput)
        }
        if (normalized == "previous song" || normalized == "previous track" || normalized == "previous" || normalized == "back track") {
            return CommandIntent(IntentType.MEDIA_PREVIOUS, rawInput)
        }
        if (normalized == "stop music" || normalized == "stop the music" || normalized == "stop") {
            return CommandIntent(IntentType.MEDIA_STOP, rawInput)
        }

        // 4. Volume Controls
        val volumeSetMatch = Regex(".*(?:set volume to|volume to|set volume)\\s+(\\d+)(?:\\s*percent|%)?.*").find(normalized)
        if (volumeSetMatch != null) {
            val percent = volumeSetMatch.groupValues[1].toIntOrNull()
            if (percent != null) {
                return CommandIntent(IntentType.VOLUME_SET, rawInput, volumePercent = percent)
            }
        }
        if (normalized.matches(Regex(".*(increase volume|volume up|turn it up|louder|raise volume).*"))) {
            return CommandIntent(IntentType.VOLUME_UP, rawInput)
        }
        if (normalized.matches(Regex(".*(decrease volume|volume down|turn it down|softer|lower volume).*"))) {
            return CommandIntent(IntentType.VOLUME_DOWN, rawInput)
        }
        if (normalized == "mute" || normalized == "mute music" || normalized == "silence") {
            return CommandIntent(IntentType.VOLUME_MUTE, rawInput)
        }

        // 5. Open App
        val openAppMatch = Regex("^(?:open|launch|start)\\s+(.+)").find(normalized)
        if (openAppMatch != null) {
            val appName = openAppMatch.groupValues[1].trim()
            return CommandIntent(IntentType.OPEN_APP, rawInput, targetApp = appName)
        }

        // 6. Weather
        if (normalized.contains("weather") || normalized.contains("rain") || normalized.contains("temperature")) {
            val isForecast = normalized.contains("tomorrow") || normalized.contains("forecast") || normalized.contains("next week")
            return if (isForecast) {
                CommandIntent(IntentType.WEATHER_FORECAST, rawInput, weatherDate = if (normalized.contains("tomorrow")) "tomorrow" else "forecast")
            } else {
                CommandIntent(IntentType.WEATHER_CURRENT, rawInput)
            }
        }

        // 7. General AI Fallback
        return CommandIntent(IntentType.GENERAL_AI, rawInput, aiQuery = normalized)
    }
}
