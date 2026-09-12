package com.example.voice

interface SpeechRecognitionListener {
    fun onReadyForSpeech()
    fun onBeginningOfSpeech()
    fun onRmsChanged(rmsdB: Float)
    fun onPartialResults(partialText: String)
    fun onResults(resultText: String)
    fun onError(errorCode: Int, errorMessage: String)
    fun onEndOfSpeech()
}

interface SpeechRecognitionProvider {
    val isAvailable: Boolean
    fun startListening(listener: SpeechRecognitionListener)
    fun stopListening()
    fun cancel()
    fun destroy()
}
