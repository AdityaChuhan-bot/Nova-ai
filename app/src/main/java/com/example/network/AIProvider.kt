package com.example.network

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

interface AIProvider {
    suspend fun getAnswer(query: String, userProvidedApiKey: String = ""): Result<String>
}

class GeminiAIProvider(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) : AIProvider {

    override suspend fun getAnswer(query: String, userProvidedApiKey: String): Result<String> =
        withContext(Dispatchers.IO) {
            val resolvedKey = when {
                userProvidedApiKey.isNotBlank() -> userProvidedApiKey.trim()
                BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
                else -> ""
            }

            if (resolvedKey.isBlank()) {
                return@withContext Result.failure(
                    Exception("Please set your Gemini API Key in Settings to enable general knowledge.")
                )
            }

            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$resolvedKey"

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", query)
                                })
                            })
                        })
                    })
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "You are Jarvis Mini, an audio smart speaker assistant. Your response will be read aloud to the user using text to speech. Answer directly, factually, and concisely in 1 or 2 spoken sentences. Do not use bullet points, markdown, asterisks, emojis, or greetings.")
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.4)
                        put("maxOutputTokens", 120)
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestJson.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errBody = response.body?.string().orEmpty()
                        return@withContext Result.failure(Exception("AI provider returned error ${response.code}: $errBody"))
                    }

                    val respString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response from AI service"))
                    val json = JSONObject(respString)
                    val candidates = json.optJSONArray("candidates")
                    if (candidates == null || candidates.length() == 0) {
                        return@withContext Result.failure(Exception("No answer generated."))
                    }

                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val rawText = parts?.optJSONObject(0)?.optString("text").orEmpty()

                    // Clean up markdown/asterisks/newlines for spoken audio output
                    val cleanSpokenText = rawText
                        .replace(Regex("[*#_`~>|\\[\\]]"), "")
                        .replace("\n", " ")
                        .replace(Regex("\\s+"), " ")
                        .trim()

                    if (cleanSpokenText.isBlank()) {
                        Result.failure(Exception("I couldn't find an answer for that."))
                    } else {
                        Result.success(cleanSpokenText)
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
