package com.yz3ro.weatherapp.ui



import WeatherRepository
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yz3ro.weatherapp.R
import com.yz3ro.weatherapp.databinding.FragmentWeatherBinding
import com.yz3ro.weatherapp.factory.WeatherViewModelFactory
import com.yz3ro.weatherapp.services.WeatherService
import com.yz3ro.weatherapp.viewmodels.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherFragment : Fragment() {
    private var binding: FragmentWeatherBinding? = null
    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
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
            Log.d("WeatherFragment", "Hava durumu verisi alındı: $weather")
            Log.e("yz3ro", "${weather.main.temp} °C")
            binding?.temperature?.text = "${weather.main.temp.toInt()} °C"
            binding?.description?.text = weather.weather.firstOrNull()?.description
            binding?.cityName?.text = weather.name

            setWeatherIcon(weather.weather.firstOrNull()?.id ?: 0)
        })


        getLocation()
    }

    private fun getLocation() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            requestLocationUpdates()
        }
    }

    private fun requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } catch (e: SecurityException) {
            Log.e("WeatherFragment", "Konum güncelleme isteği başarısız", e)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            println(latitude)
            val longitude = location.longitude
            println(longitude)
            val apiKey = "82186ab625733582a8ebaae0c6865395"

            viewModel.getWeather(latitude, longitude, apiKey)
        }

        override fun onProviderDisabled(provider: String) {
            Log.e("WeatherFragment", "Konum sağlayıcı devre dışı bırakıldı: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d("WeatherFragment", "Konum sağlayıcı etkinleştirildi: $provider")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.d("WeatherFragment", "Konum sağlayıcı durumu değişti: $status")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            locationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Konum izni reddedildi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setWeatherIcon(weatherCode: Int) {
        val iconResource = when (weatherCode) {
            in 200..232 -> R.drawable.ic_thunderstorm
            in 300..321 -> R.drawable.ic_drizzle
            in 500..531 -> R.drawable.ic_rain
            in 600..622 -> R.drawable.ic_snow
            in 701..781 -> R.drawable.ic_fog
            800 -> R.drawable.ic_clear_sky
            in 801..804 -> R.drawable.ic_cloudy
            else -> {
                Log.e("WeatherFragment", "Bilinmeyen hava durumu kodu: $weatherCode")
                return
            }
        }

        binding?.WeatherIcon?.setImageResource(iconResource)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
