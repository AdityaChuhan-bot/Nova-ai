package com.example.domain

import com.example.data.model.CommandIntent
import com.example.data.model.IntentType
import com.example.data.preferences.JarvisSettings
import com.example.device.AppController
import com.example.device.AppLaunchResult
import com.example.device.MediaController
import com.example.device.SystemController
import com.example.device.VolumeController
import com.example.network.AIProvider
import com.example.network.NetworkMonitor
import com.example.network.WeatherProvider

data class RouterResult(
    val spokenResponse: String,
    val isSuccess: Boolean,
    val intentType: IntentType,
    val usedCloudAi: Boolean = false
)

class CommandRouter(
    private val appController: AppController,
    private val mediaController: MediaController,
    private val volumeController: VolumeController,
    private val systemController: SystemController,
    private val weatherProvider: WeatherProvider,
    private val aiProvider: AIProvider,
    private val networkMonitor: NetworkMonitor
) {

    suspend fun execute(intent: CommandIntent, settings: JarvisSettings): RouterResult {
        return when (intent.type) {
            // Local Commands - No Internet Needed
            IntentType.TIME -> {
                val time = systemController.getCurrentTime()
                RouterResult(spokenResponse = time, isSuccess = true, intentType = intent.type)
            }

            IntentType.DATE -> {
                val date = systemController.getCurrentDate()
                RouterResult(spokenResponse = date, isSuccess = true, intentType = intent.type)
            }

            IntentType.MEDIA_PLAY, IntentType.MEDIA_RESUME -> {
                val feedback = mediaController.play()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.MEDIA_PAUSE -> {
                val feedback = mediaController.pause()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.MEDIA_STOP -> {
                val feedback = mediaController.stop()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.MEDIA_NEXT -> {
                val feedback = mediaController.next()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.MEDIA_PREVIOUS -> {
                val feedback = mediaController.previous()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.VOLUME_UP -> {
                val feedback = volumeController.increaseVolume()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.VOLUME_DOWN -> {
                val feedback = volumeController.decreaseVolume()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.VOLUME_SET -> {
                val percent = intent.volumePercent ?: 50
                val feedback = volumeController.setVolumePercent(percent)
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.VOLUME_MUTE -> {
                val feedback = volumeController.mute()
                RouterResult(spokenResponse = feedback, isSuccess = true, intentType = intent.type)
            }

            IntentType.OPEN_APP -> {
                val targetApp = intent.targetApp.orEmpty()
                when (val launchResult = appController.openApp(targetApp, settings.defaultMusicPackage)) {
                    is AppLaunchResult.Success -> {
                        RouterResult(spokenResponse = launchResult.spokenFeedback, isSuccess = true, intentType = intent.type)
                    }
                    is AppLaunchResult.Failure -> {
                        RouterResult(spokenResponse = launchResult.spokenFeedback, isSuccess = false, intentType = intent.type)
                    }
                }
            }

            // Weather Commands - Requires Internet
            IntentType.WEATHER_CURRENT, IntentType.WEATHER_FORECAST -> {
                if (!networkMonitor.isOnline()) {
                    return RouterResult(
                        spokenResponse = "I need an internet connection for weather updates.",
                        isSuccess = false,
                        intentType = intent.type
                    )
                }

                val city = settings.weatherCity
                val unit = settings.weatherUnit
                val isForecast = intent.type == IntentType.WEATHER_FORECAST

                val weatherResult = if (isForecast) {
                    weatherProvider.getWeatherForecast(city, unit, intent.weatherDate ?: "today")
                } else {
                    weatherProvider.getCurrentWeather(city, unit)
                }

                weatherResult.fold(
                    onSuccess = { response ->
                        RouterResult(spokenResponse = response, isSuccess = true, intentType = intent.type)
                    },
                    onFailure = { error ->
                        RouterResult(
                            spokenResponse = "Couldn't retrieve weather: ${error.message ?: "service unavailable"}",
                            isSuccess = false,
                            intentType = intent.type
                        )
                    }
                )
            }

            // General Knowledge / AI Fallback
            IntentType.GENERAL_AI -> {
                if (!settings.enableAiFallback) {
                    return RouterResult(
                        spokenResponse = "I can only perform local device and media commands right now.",
                        isSuccess = false,
                        intentType = intent.type
                    )
                }

                if (!networkMonitor.isOnline()) {
                    return RouterResult(
                        spokenResponse = "I'm offline. I can still control apps and perform basic device commands.",
                        isSuccess = false,
                        intentType = intent.type
                    )
                }

                val query = intent.aiQuery ?: intent.rawText
                val aiResult = aiProvider.getAnswer(query, settings.customGeminiApiKey)

                aiResult.fold(
                    onSuccess = { answer ->
                        RouterResult(
                            spokenResponse = answer,
                            isSuccess = true,
                            intentType = intent.type,
                            usedCloudAi = true
                        )
                    },
                    onFailure = { err ->
                        RouterResult(
                            spokenResponse = err.message ?: "The AI service isn't available right now.",
                            isSuccess = false,
                            intentType = intent.type,
                            usedCloudAi = true
                        )
                    }
                )
            }

            IntentType.UNKNOWN -> {
                RouterResult(
                    spokenResponse = "Sorry, I didn't catch that.",
                    isSuccess = false,
                    intentType = intent.type
                )
            }
        }
    }
}
