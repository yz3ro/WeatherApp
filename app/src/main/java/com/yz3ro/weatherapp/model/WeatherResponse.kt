package com.yz3ro.weatherapp.model

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String,
)


