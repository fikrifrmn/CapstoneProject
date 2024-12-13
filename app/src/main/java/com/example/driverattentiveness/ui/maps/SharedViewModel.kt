package com.example.driverattentiveness.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    val totalDistance = MutableLiveData<Float>()
}