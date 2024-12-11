package com.example.driverattentiveness.ui.main

<<<<<<< HEAD
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
=======
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.driverattentiveness.R
<<<<<<< HEAD
import com.example.driverattentiveness.ViewModelFactory
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import com.example.driverattentiveness.databinding.ActivityMainBinding
import com.example.driverattentiveness.ui.welcome.WelcomeFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
=======
import com.example.driverattentiveness.databinding.ActivityMainBinding

//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.bottomNavigationView
//
//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_login, R.id.navigation_signup, R.id.navigation_welcome
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//    }
//}
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.bottomNavigationView

        // Temukan NavController melalui NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Konfigurasi AppBar untuk destinasi level atas
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
<<<<<<< HEAD

=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
}