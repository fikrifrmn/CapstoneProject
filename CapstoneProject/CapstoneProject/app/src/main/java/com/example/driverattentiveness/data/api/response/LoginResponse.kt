package com.example.driverattentiveness.data.api.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class Data(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("token")
    val token: String? = null
)
