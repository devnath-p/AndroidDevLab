package com.example.exp12

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : ComponentActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMessage: TextView

    private lateinit var weatherLayout: LinearLayout
    private lateinit var tvCity: TextView
    private lateinit var tvWeatherIcon: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWind: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etCity = findViewById(R.id.etCity)
        btnSearch = findViewById(R.id.btnSearch)
        progressBar = findViewById(R.id.progressBar)
        tvMessage = findViewById(R.id.tvMessage)

        weatherLayout = findViewById(R.id.weatherLayout)
        tvCity = findViewById(R.id.tvCity)
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvCondition = findViewById(R.id.tvCondition)
        tvFeelsLike = findViewById(R.id.tvFeelsLike)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWind = findViewById(R.id.tvWind)

        // Search button click
        btnSearch.setOnClickListener {

            val city = etCity.text.toString().trim()

            if (city.isEmpty()) {
                etCity.error = "Enter a city name"
                return@setOnClickListener
            }

            fetchWeather(city)
        }
    }

    // Fetch weather data in background thread
    private fun fetchWeather(city: String) {

        progressBar.visibility = View.VISIBLE
        weatherLayout.visibility = View.GONE
        tvMessage.text = "Fetching weather data..."
        btnSearch.isEnabled = false

        Thread {

            try {

                // STEP 1: Get city coordinates
                val encodedCity =
                    URLEncoder.encode(city, "UTF-8")

                val geoUrl =
                    "https://geocoding-api.open-meteo.com/v1/search" +
                            "?name=$encodedCity&count=1&language=en&format=json"

                val geoResponse = JSONObject(
                    makeHttpRequest(geoUrl)
                )

                val locations =
                    geoResponse.optJSONArray("results")
                        ?: throw Exception("City not found!")

                if (locations.length() == 0) {
                    throw Exception("City not found!")
                }

                val location = locations.getJSONObject(0)

                val latitude = location.getDouble("latitude")
                val longitude = location.getDouble("longitude")

                val cityName = location.getString("name")
                val country = location.optString("country", "")

                // STEP 2: Get weather information
                val weatherUrl =
                    "https://api.open-meteo.com/v1/forecast" +
                            "?latitude=$latitude" +
                            "&longitude=$longitude" +
                            "&current=temperature_2m," +
                            "relative_humidity_2m," +
                            "apparent_temperature," +
                            "weather_code,wind_speed_10m" +
                            "&timezone=auto"

                val weatherResponse = JSONObject(
                    makeHttpRequest(weatherUrl)
                )

                val current =
                    weatherResponse.getJSONObject("current")

                val temperature =
                    current.getDouble("temperature_2m")

                val feelsLike =
                    current.getDouble("apparent_temperature")

                val humidity =
                    current.getInt("relative_humidity_2m")

                val wind =
                    current.getDouble("wind_speed_10m")

                val weatherCode =
                    current.getInt("weather_code")

                // STEP 3: Identify weather condition
                val condition = when (weatherCode) {

                    0 -> "Clear Sky"
                    1 -> "Mainly Clear"
                    2 -> "Partly Cloudy"
                    3 -> "Overcast"
                    45, 48 -> "Foggy"

                    in 51..57 -> "Drizzle"
                    in 61..67 -> "Rainy"
                    in 71..77 -> "Snowfall"
                    in 80..82 -> "Rain Showers"
                    in 85..86 -> "Snow Showers"
                    in 95..99 -> "Thunderstorm"

                    else -> "Unknown"
                }

                val icon = when (weatherCode) {

                    0, 1 -> "☀️"
                    2, 3 -> "⛅"
                    45, 48 -> "🌫️"
                    in 51..67, in 80..82 -> "🌧️"
                    in 71..77, in 85..86 -> "❄️"
                    in 95..99 -> "⛈️"

                    else -> "🌤️"
                }

                // STEP 4: Update UI
                runOnUiThread {

                    tvCity.text = "$cityName, $country"
                    tvWeatherIcon.text = icon

                    tvTemperature.text =
                        "${temperature}°C"

                    tvCondition.text = condition

                    tvFeelsLike.text =
                        "${feelsLike}°C"

                    tvHumidity.text =
                        "$humidity%"

                    tvWind.text =
                        "$wind km/h"

                    weatherLayout.visibility = View.VISIBLE

                    tvMessage.text =
                        "Weather updated successfully!"

                    progressBar.visibility = View.GONE
                    btnSearch.isEnabled = true
                }

            } catch (e: Exception) {

                runOnUiThread {

                    tvMessage.text =
                        "Error: ${e.message ?: "Unable to fetch weather"}"

                    weatherLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    btnSearch.isEnabled = true
                }
            }

        }.start()
    }

    // HTTP GET request function
    private fun makeHttpRequest(urlString: String): String {

        val url = URL(urlString)

        val connection =
            url.openConnection() as HttpURLConnection

        try {

            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            val responseCode = connection.responseCode

            if (responseCode !in 200..299) {
                throw Exception("HTTP Error: $responseCode")
            }

            return connection.inputStream
                .bufferedReader()
                .use { it.readText() }

        } finally {
            connection.disconnect()
        }
    }
}
