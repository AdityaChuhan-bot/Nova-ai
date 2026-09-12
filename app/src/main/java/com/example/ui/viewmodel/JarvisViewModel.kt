package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AssistantState
import com.example.data.preferences.JarvisPreferences
import com.example.data.preferences.JarvisSettings
import com.example.data.preferences.OperationMode
import com.example.device.AppController
import com.example.device.AppInfo
import com.example.device.MediaController
import com.example.device.SystemController
import com.example.device.VolumeController
import com.example.domain.CommandRouter
import com.example.domain.IntentClassifier
import com.example.network.GeminiAIProvider
import com.example.network.NetworkMonitor
import com.example.network.OpenMeteoWeatherProvider
import com.example.service.JarvisForegroundService
import com.example.voice.AndroidSpeechRecognizerProvider
import com.example.voice.SpeechRecognitionListener
import com.example.voice.SpeechRecognitionProvider
import com.example.voice.TextToSpeechProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()

    private val jarvisPreferences = JarvisPreferences(context)
    private val _settings = MutableStateFlow(jarvisPreferences.loadSettings())
    val settings: StateFlow<JarvisSettings> = _settings.asStateFlow()

    private val _assistantState = MutableStateFlow(AssistantState.IDLE)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _responseText = MutableStateFlow("Tap the microphone or say \"Hey Jarvis\" to start.")
    val responseText: StateFlow<String> = _responseText.asStateFlow()

    private val _audioRmsLevel = MutableStateFlow(0f)
    val audioRmsLevel: StateFlow<Float> = _audioRmsLevel.asStateFlow()

    private val _currentTimeString = MutableStateFlow("")
    val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

    private val _currentDateString = MutableStateFlow("")
    val currentDateString: StateFlow<String> = _currentDateString.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _currentVolume = MutableStateFlow(50)
    val currentVolume: StateFlow<Int> = _currentVolume.asStateFlow()

    // Device & Domain components
    val appController = AppController(context)
    val mediaController = MediaController(context)
    val volumeController = VolumeController(context)
    val systemController = SystemController(context)
    private val weatherProvider = OpenMeteoWeatherProvider()
    private val aiProvider = GeminiAIProvider()
    private val networkMonitor = NetworkMonitor(context)
    private val intentClassifier = IntentClassifier()

    private val commandRouter = CommandRouter(
        appController = appController,
        mediaController = mediaController,
        volumeController = volumeController,
        systemController = systemController,
        weatherProvider = weatherProvider,
        aiProvider = aiProvider,
        networkMonitor = networkMonitor
    )

    private val speechRecognizerProvider: SpeechRecognitionProvider = AndroidSpeechRecognizerProvider(context)
    private var textToSpeechProvider: TextToSpeechProvider? = null

    private var sessionTimeoutJob: Job? = null
    private var clockJob: Job? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        initTts()
        refreshInstalledApps()
        startClockUpdates()
        refreshVolume()
        _isOnline.value = networkMonitor.isOnline()

        if (_settings.value.operationMode == OperationMode.ALWAYS_LISTENING) {
            JarvisForegroundService.start(context)
        }
    }

    private fun initTts() {
        textToSpeechProvider = TextToSpeechProvider(context) { isReady ->
            if (isReady && _settings.value.operationMode == OperationMode.ALWAYS_LISTENING) {
                startWakeWordListening()
            }
        }
    }

    private fun startClockUpdates() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            while (isActive) {
                val now = Date()
                _currentTimeString.value = timeFormat.format(now)
                _currentDateString.value = dateFormat.format(now)
                _isOnline.value = networkMonitor.isOnline()
                delay(1000)
            }
        }
    }

    fun refreshVolume() {
        _currentVolume.value = volumeController.getCurrentVolumePercent()
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = appController.getInstalledApps()
        }
    }

    fun onMicTapped() {
        when (_assistantState.value) {
            AssistantState.SPEAKING -> {
                stopSpeaking()
                _assistantState.value = AssistantState.IDLE
            }
            AssistantState.LISTENING -> {
                speechRecognizerProvider.stopListening()
            }
            AssistantState.PROCESSING, AssistantState.EXECUTING -> {
                // Ignore while executing
            }
            else -> {
                startSpeechRecognition(isWakeWordTrigger = false)
            }
        }
    }

    fun onStopSpeakingTapped() {
        stopSpeaking()
        resetToIdleOrWakeWord()
    }

    fun executeTextCommand(commandText: String) {
        if (commandText.isBlank()) return
        _spokenText.value = commandText
        processCommand(commandText)
    }

    private fun startSpeechRecognition(isWakeWordTrigger: Boolean) {
        stopSpeaking()
        sessionTimeoutJob?.cancel()

        _assistantState.value = AssistantState.LISTENING
        _spokenText.value = if (isWakeWordTrigger) "Listening..." else ""

        speechRecognizerProvider.startListening(object : SpeechRecognitionListener {
            override fun onReadyForSpeech() {
                _assistantState.value = AssistantState.LISTENING
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize rmsdB (-2 to 10 typical range) into 0.0 .. 1.0 for UI visualizer
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1.0f)
                _audioRmsLevel.value = normalized
            }

            override fun onPartialResults(partialText: String) {
                _spokenText.value = partialText
            }

            override fun onResults(resultText: String) {
                _audioRmsLevel.value = 0f
                if (resultText.isNotBlank()) {
                    _spokenText.value = resultText
                    processCommand(resultText)
                } else {
                    handleNoSpeechDetected()
                }
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                _audioRmsLevel.value = 0f
                if (_assistantState.value == AssistantState.LISTENING) {
                    _responseText.value = "Sorry, I didn't catch that."
                    _assistantState.value = AssistantState.ERROR
                    mainHandler.postDelayed({
                        resetToIdleOrWakeWord()
                    }, 2500)
                }
            }

            override fun onEndOfSpeech() {
                _audioRmsLevel.value = 0f
                if (_assistantState.value == AssistantState.LISTENING) {
                    _assistantState.value = AssistantState.PROCESSING
                }
            }
        })
    }

    private fun handleNoSpeechDetected() {
        _assistantState.value = AssistantState.IDLE
        resetToIdleOrWakeWord()
    }

    private fun processCommand(rawText: String) {
        _assistantState.value = AssistantState.PROCESSING

        viewModelScope.launch {
            val intent = intentClassifier.classify(rawText)
            _assistantState.value = AssistantState.EXECUTING

            val result = commandRouter.execute(intent, _settings.value)
            _responseText.value = result.spokenResponse
            refreshVolume()

            speakResponse(result.spokenResponse)
        }
    }

    private fun speakResponse(textToSpeak: String) {
        _assistantState.value = AssistantState.SPEAKING

        textToSpeechProvider?.speak(
            text = textToSpeak,
            speechRate = _settings.value.speechSpeed,
            pitch = _settings.value.speechPitch,
            onStart = {
                _assistantState.value = AssistantState.SPEAKING
            },
            onDone = {
                handleSpeechComplete()
            }
        )
    }

    private fun handleSpeechComplete() {
        mainHandler.post {
            when (_settings.value.operationMode) {
                OperationMode.VOICE_SESSION -> {
                    // Voice Session: stay ready and listen for followup command for configurable timeout
                    _assistantState.value = AssistantState.IDLE
                    startVoiceSessionTimeout()
                }
                OperationMode.ALWAYS_LISTENING -> {
                    startWakeWordListening()
                }
                OperationMode.PUSH_TO_TALK -> {
                    _assistantState.value = AssistantState.IDLE
                }
            }
        }
    }

    private fun startVoiceSessionTimeout() {
        sessionTimeoutJob?.cancel()
        sessionTimeoutJob = viewModelScope.launch {
            val timeout = _settings.value.sessionTimeoutSeconds * 1000L
            delay(timeout)
            if (_assistantState.value == AssistantState.IDLE) {
                resetToIdleOrWakeWord()
            }
        }
    }

    fun startWakeWordListening() {
        if (_settings.value.operationMode != OperationMode.ALWAYS_LISTENING) return

        _assistantState.value = AssistantState.WAKE_WORD_LISTENING

        speechRecognizerProvider.startListening(object : SpeechRecognitionListener {
            override fun onReadyForSpeech() {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onPartialResults(partialText: String) {
                checkWakeWordTrigger(partialText)
            }
            override fun onResults(resultText: String) {
                if (!checkWakeWordTrigger(resultText)) {
                    // If not triggered, seamlessly loop back to listening for wake word
                    if (_assistantState.value == AssistantState.WAKE_WORD_LISTENING) {
                        mainHandler.postDelayed({
                            if (_assistantState.value == AssistantState.WAKE_WORD_LISTENING) {
                                startWakeWordListening()
                            }
                        }, 500)
                    }
                }
            }
            override fun onError(errorCode: Int, errorMessage: String) {
                // If wake word loop times out or detects silence, quietly restart
                if (_assistantState.value == AssistantState.WAKE_WORD_LISTENING) {
                    mainHandler.postDelayed({
                        if (_assistantState.value == AssistantState.WAKE_WORD_LISTENING) {
                            startWakeWordListening()
                        }
                    }, 800)
                }
            }
            override fun onEndOfSpeech() {}
        })
    }

    private fun checkWakeWordTrigger(text: String): Boolean {
        val lower = text.lowercase()
        val targetWakeWord = _settings.value.wakeWord.lowercase()
        if (lower.contains(targetWakeWord) || lower.contains("hey jarvis") || lower.contains("jarvis")) {
            playActivationTone()
            _assistantState.value = AssistantState.ACTIVATED
            mainHandler.postDelayed({
                startSpeechRecognition(isWakeWordTrigger = true)
            }, 300)
            return true
        }
        return false
    }

    private fun playActivationTone() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (e: Exception) {
            // Ignore tone failure
        }
    }

    private fun resetToIdleOrWakeWord() {
        if (_settings.value.operationMode == OperationMode.ALWAYS_LISTENING) {
            startWakeWordListening()
        } else {
            _assistantState.value = AssistantState.IDLE
        }
    }

    fun stopSpeaking() {
        textToSpeechProvider?.stop()
        if (_assistantState.value == AssistantState.SPEAKING) {
            _assistantState.value = AssistantState.IDLE
        }
    }

    fun updateSettings(newSettings: JarvisSettings) {
        _settings.value = newSettings
        jarvisPreferences.saveSettings(newSettings)

        if (newSettings.operationMode == OperationMode.ALWAYS_LISTENING) {
            JarvisForegroundService.start(context)
            startWakeWordListening()
        } else {
            JarvisForegroundService.stop(context)
            if (_assistantState.value == AssistantState.WAKE_WORD_LISTENING) {
                speechRecognizerProvider.stopListening()
                _assistantState.value = AssistantState.IDLE
            }
        }
    }

    // Media and volume quick helpers for UI cards
    fun togglePlayPause() {
        val msg = mediaController.playPauseToggle()
        _responseText.value = msg
    }

    fun nextTrack() {
        val msg = mediaController.next()
        _responseText.value = msg
    }

    fun previousTrack() {
        val msg = mediaController.previous()
        _responseText.value = msg
    }

    fun setVolume(percent: Int) {
        val msg = volumeController.setVolumePercent(percent)
        _responseText.value = msg
        refreshVolume()
    }

    fun launchApp(packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
        sessionTimeoutJob?.cancel()
        speechRecognizerProvider.destroy()
        textToSpeechProvider?.destroy()
    }
}
