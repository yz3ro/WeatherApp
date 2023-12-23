package com.yz3ro.weatherapp.viewmodels

import WeatherRepository
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yz3ro.weatherapp.model.WeatherResponse
import kotlinx.coroutines.launch


class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse>
        get() = _weather

    fun getWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(lat, lon, apiKey)
                _weather.value = response
            } catch (e: Exception) {
            }
        }
    }
}




