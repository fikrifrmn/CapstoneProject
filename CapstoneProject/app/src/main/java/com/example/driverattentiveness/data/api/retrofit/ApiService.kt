package com.example.driverattentiveness.data.api.retrofit

import com.example.driverattentiveness.data.api.response.ArticleResponse
import com.example.driverattentiveness.data.api.response.LoginResponse
import com.example.driverattentiveness.data.api.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("v2/top-headlines")
    fun getData(
        @Query("q") query: String = "crash",
        @Query("apiKey") apiKey: String
    ): Call<ArticleResponse>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("age") age: String
    ): RegisterResponse
}