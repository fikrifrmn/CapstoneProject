package com.example.driverattentiveness.ui.maps

data class TripRequest(
    val id: Int,  // Trip ID yang disimpan di preferences
    val userId: Int,
    val startLocation: String,
    val endLocation: String,
    val startTime: String,
    val endTime: String? = null,
    val tripId: Int  // ID trip yang akan dipakai untuk membuat trip baru
)