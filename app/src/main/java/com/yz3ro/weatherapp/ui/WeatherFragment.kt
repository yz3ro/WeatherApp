package com.yz3ro.weatherapp.ui

import WeatherRepository
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yz3ro.weatherapp.R
import com.yz3ro.weatherapp.databinding.FragmentHomeBinding
import com.yz3ro.weatherapp.factory.WeatherViewModelFactory
import com.yz3ro.weatherapp.services.WeatherService
import com.yz3ro.weatherapp.viewmodels.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// WeatherFragment.kt
class WeatherFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val service = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        val repository = WeatherRepository(service)
        viewModel = ViewModelProvider(this, WeatherViewModelFactory(repository))
            .get(WeatherViewModel::class.java)

        viewModel.weather.observe(viewLifecycleOwner, Observer { weather ->
            // Hava durumu verilerini kullanarak UI güncellemeleri yapılabilir
            Log.e("yz3ro", "${weather.main.temp} °C")
            binding?.temperature?.text = "${weather.main.temp.toInt()} °C"
            binding?.description?.text = weather.weather.firstOrNull()?.description
            binding?.cityName?.text = weather.name

            // Hava durumuna göre ikonu ayarla
            setWeatherIcon(weather.weather.firstOrNull()?.id ?: 0)
        })

        getLocation()
    }

    private fun getLocation() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val latitude =location.latitude
                val longitude =location.longitude
                val apiKey = "d5357b741c4266ced97a97d0459f5758"

                viewModel.getWeather(latitude, longitude, apiKey)
            } else {
                // Handle location is null
            }
        } else {
            // Handle location permission not granted
        }
    }
    private fun setWeatherIcon(weatherCode: Int) {
        val iconResource = when (weatherCode) {
            in 200..232 -> R.drawable.ic_thunderstorm // Fırtına
            in 300..321 -> R.drawable.ic_drizzle // Çisenti
            in 500..531 -> R.drawable.ic_rain // Yağmur
            in 600..622 -> R.drawable.ic_snow // Kar
            in 701..781 -> R.drawable.ic_fog // Sis
            800 -> R.drawable.ic_clear_sky // Açık Hava
            in 801..804 -> R.drawable.ic_cloudy // Bulutlu
            else -> {}
        }

        binding?.WeatherIcon?.setImageResource(iconResource as Int)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

