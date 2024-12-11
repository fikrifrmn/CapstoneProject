package com.example.driverattentiveness.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.driverattentiveness.data.UserRepository

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    val userSession = repository.getSession().asLiveData()
}