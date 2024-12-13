package com.example.driverattentiveness.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val name: String = "",
    val id: String = "",
    val age: Int = 0,

    val tripId: String = "",
    val alertCount: Int = 0
)