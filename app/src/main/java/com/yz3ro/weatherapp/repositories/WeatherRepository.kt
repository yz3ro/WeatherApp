import com.yz3ro.weatherapp.model.WeatherResponse
import com.yz3ro.weatherapp.services.WeatherService
import retrofit2.Call
import retrofit2.Response



// WeatherRepository.kt
class WeatherRepository(private val service: WeatherService) {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): WeatherResponse {
        val response = service.getWeatherByLocation(latitude, longitude, apiKey)
        if (response.isSuccessful) {
            val weatherResponse = response.body()

            // Kelvin cinsinden sıcaklık değerini Celsius'a dönüştür
            weatherResponse?.main?.temp = kelvinToCelsius(weatherResponse?.main?.temp ?: 0.0)

            return weatherResponse ?: throw Exception("Weather data is null")
        } else {
            throw Exception("Error ${response.code()}: ${response.message()}")
        }
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }
}



