package com.example.driverattentiveness.data.api.retrofit

import com.example.driverattentiveness.data.api.response.AllTripResponse
import com.example.driverattentiveness.data.api.response.ArticleResponse
import com.example.driverattentiveness.data.api.response.CreateTripResponse
import com.example.driverattentiveness.data.api.response.DataCreateTrip
import com.example.driverattentiveness.data.api.response.LoginResponse
import com.example.driverattentiveness.data.api.response.RegisterResponse
import com.example.driverattentiveness.data.api.response.UpdateResponse
import com.example.driverattentiveness.data.api.response.UpdateTripResponse
import com.example.driverattentiveness.ui.maps.TripRequest
import com.example.driverattentiveness.ui.setting.UserUpdateRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @PUT("auth/profile/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UserUpdateRequest
    ): Response<UpdateResponse>

    @FormUrlEncoded
    @POST("trip")
    suspend fun createTrip(
        @Field("start_location") startLocation: String,
        @Field("start_time") startTime: String,
        @Field("user_id") userId: Int,
        @Field("id") id: Int
    ): Response<CreateTripResponse>

    @FormUrlEncoded
    @PUT("trip/{id}")
    suspend fun updateTrip(
        @Path("id") id: Int,
        @Field("user_id") userId: Int,
        @Field("end_location") endLocation: String,
        @Field("end_time") endTime: String
    ): Response<UpdateTripResponse>

}