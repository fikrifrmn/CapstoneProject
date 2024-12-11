package com.example.driverattentiveness.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.driverattentiveness.R
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import com.example.driverattentiveness.ui.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()


        Handler(Looper.getMainLooper()).postDelayed({
            wasLoginned()
        }, 2000)

    }

    private fun wasLoginned() {
        // Inisialisasi UserRepository
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)

        lifecycleScope.launch {
            val userSession = userPreference.getSession().first()
            val apiService = ApiConfig.getApiService(userSession.token)
            userRepository = UserRepository.getInstance(userPreference, apiService)

            val intent = Intent(this@SplashScreen, MainActivity::class.java).apply {
                putExtra("isLogin", userSession.isLogin)
            }
            startActivity(intent)

            finish()
        }
    }
}