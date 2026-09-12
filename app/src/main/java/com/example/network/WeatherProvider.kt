package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

interface WeatherProvider {
    suspend fun getCurrentWeather(city: String, unit: String): Result<String>
    suspend fun getWeatherForecast(city: String, unit: String, dateContext: String = "today"): Result<String>
}

class OpenMeteoWeatherProvider(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
) : WeatherProvider {

    override suspend fun getCurrentWeather(city: String, unit: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val coords = getCoordinates(city)
                    ?: return@withContext Result.failure(Exception("Could not find location for $city"))

                val tempUnitParam = if (unit.equals("Fahrenheit", ignoreCase = true)) "&temperature_unit=fahrenheit" else ""
                val unitSymbol = if (unit.equals("Fahrenheit", ignoreCase = true)) "degrees Fahrenheit" else "degrees Celsius"

                val url = "https://api.open-meteo.com/v1/forecast?latitude=${coords.latitude}&longitude=${coords.longitude}&current=temperature_2m,weather_code,wind_speed_10m$tempUnitParam&timezone=auto"

                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("Weather service returned error: ${response.code}"))
                    }
                    val bodyString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty weather response"))
                    val json = JSONObject(bodyString)
                    val current = json.getJSONObject("current")
                    val temp = current.getDouble("temperature_2m").roundToInt()
                    val code = current.getInt("weather_code")
                    val condition = weatherCodeToDescription(code)

                    Result.success("It's currently $temp $unitSymbol and $condition in ${coords.name}.")
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getWeatherForecast(city: String, unit: String, dateContext: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val coords = getCoordinates(city)
                    ?: return@withContext Result.failure(Exception("Could not find location for $city"))

                val tempUnitParam = if (unit.equals("Fahrenheit", ignoreCase = true)) "&temperature_unit=fahrenheit" else ""
                val unitSymbol = if (unit.equals("Fahrenheit", ignoreCase = true)) "degrees Fahrenheit" else "degrees Celsius"

                val url = "https://api.open-meteo.com/v1/forecast?latitude=${coords.latitude}&longitude=${coords.longitude}&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max$tempUnitParam&timezone=auto"

                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("Weather service error: ${response.code}"))
                    }
                    val bodyString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty forecast response"))
                    val json = JSONObject(bodyString)
                    val daily = json.getJSONObject("daily")

                    val isTomorrow = dateContext.contains("tomorrow", ignoreCase = true)
                    val dayIndex = if (isTomorrow) 1 else 0
                    val dayLabel = if (isTomorrow) "Tomorrow" else "Today"

                    val weatherCodes = daily.getJSONArray("weather_code")
                    val maxTemps = daily.getJSONArray("temperature_2m_max")
                    val minTemps = daily.getJSONArray("temperature_2m_min")
                    val rainProb = daily.optJSONArray("precipitation_probability_max")

                    val code = weatherCodes.getInt(dayIndex)
                    val maxTemp = maxTemps.getDouble(dayIndex).roundToInt()
                    val minTemp = minTemps.getDouble(dayIndex).roundToInt()
                    val condition = weatherCodeToDescription(code)
                    val rainChance = if (rainProb != null && rainProb.length() > dayIndex) rainProb.getInt(dayIndex) else null

                    val rainStatement = if (rainChance != null && rainChance > 20) {
                        " with a $rainChance percent chance of rain"
                    } else ""

                    Result.success("$dayLabel in ${coords.name} will be $condition$rainStatement, with a high of $maxTemp and low of $minTemp $unitSymbol.")
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun getCoordinates(city: String): LocationCoordinates? {
        return try {
            val encodedCity = java.net.URLEncoder.encode(city, "UTF-8")
            val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=en&format=json"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return null
                if (results.length() == 0) return null
                val first = results.getJSONObject(0)
                LocationCoordinates(
                    name = first.optString("name", city),
                    latitude = first.getDouble("latitude"),
                    longitude = first.getDouble("longitude")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun weatherCodeToDescription(code: Int): String {
        return when (code) {
            0 -> "clear skies"
            1, 2, 3 -> "partly cloudy"
            45, 48 -> "foggy"
            51, 53, 55 -> "drizzle"
            56, 57 -> "freezing drizzle"
            61, 63, 65 -> "rain"
            66, 67 -> "freezing rain"
            71, 73, 75, 77 -> "snow"
            80, 81, 82 -> "rain showers"
            85, 86 -> "snow showers"
            95 -> "thunderstorms"
            96, 99 -> "thunderstorms with hail"
            else -> "fair"
        }
    }

    data class LocationCoordinates(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )
}
