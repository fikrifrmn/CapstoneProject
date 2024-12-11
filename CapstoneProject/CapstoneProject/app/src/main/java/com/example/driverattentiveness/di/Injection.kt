package com.example.driverattentiveness.di

import android.content.Context
import com.example.driverattentiveness.data.UserRepository
<<<<<<< HEAD
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
=======
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
<<<<<<< HEAD
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref,apiService)
=======
        return UserRepository.getInstance(pref)
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
    }
}