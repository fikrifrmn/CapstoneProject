package com.example.driverattentiveness.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.driverattentiveness.R
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import com.example.driverattentiveness.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fab: FloatingActionButton = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            // Pop semua fragment yang ada di back stack, agar kembali ke HomeFragment
            findNavController(R.id.nav_host_fragment).popBackStack()

            // Lanjutkan untuk navigasi ke CameraFragment
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_camera)
        }

        val isLogin = intent.getBooleanExtra("isLogin", false)

        // Inisialisasi UserRepository
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        userRepository = UserRepository.getInstance(userPreference, ApiConfig.getApiService(""))

        // Temukan NavController melalui NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController



        // Update graph startDestination
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination(
            if (isLogin) R.id.navigation_home else R.id.navigation_welcome
        )
        navController.graph = navGraph

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_camera, R.id.navigation_maps, R.id.navigation_profile)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.navigation_home) {
            finish() // Keluar aplikasi jika di Home
        } else {
            super.onBackPressed() // Navigasi back default
        }
    }
}