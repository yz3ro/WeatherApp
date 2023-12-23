import android.util.Log
import com.yz3ro.weatherapp.model.WeatherResponse
import com.yz3ro.weatherapp.services.WeatherService


class WeatherRepository(private val service: WeatherService) {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): WeatherResponse {
        try {
            val response = service.getWeatherByLocation(latitude, longitude, apiKey)
            if (response.isSuccessful) {
                val weatherResponse = response.body()

                // Kelvin cinsinden sıcaklık değerini Celsius'a dönüştür
                weatherResponse?.main?.temp = kelvinToCelsius(weatherResponse?.main?.temp ?: 0.0)

                // Log: Başarıyla hava durumu verisi alındı
                Log.d("WeatherRepository", "Başarıyla hava durumu verisi alındı: $weatherResponse")

                return weatherResponse ?: throw Exception("Weather data is null")
            } else {
                // Log: Hata durumunda
                Log.e("WeatherRepository", "Error ${response.code()}: ${response.message()}")
                throw Exception("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            // Log: Genel bir hata durumunda
            Log.e("WeatherRepository", "Genel bir hata oluştu: ${e.message}")
            throw e
        }
    }
    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }
}



