package com.yz3ro.weatherapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.yz3ro.weatherapp.R

class MainActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 2000 // Millisaniye cinsinden süre
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}