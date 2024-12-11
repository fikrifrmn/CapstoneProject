package com.example.driverattentiveness.data.api.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("error")
    val error: String? = null
)