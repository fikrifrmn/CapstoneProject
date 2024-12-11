package com.example.driverattentiveness.data

import com.example.driverattentiveness.data.api.response.LoginResponse
import com.example.driverattentiveness.data.api.response.UpdateResponse
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.api.retrofit.ApiService
import com.example.driverattentiveness.data.pref.UserModel
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.ui.setting.UserUpdateRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun updateUser(id: String, request: UserUpdateRequest): UpdateResponse {
        val token = userPreference.getSession().first().token
        val apiServiceWithToken = ApiConfig.getApiService(token)

        // Mengirim request ke API
        val response = apiServiceWithToken.updateUser(id, request)

        // Jika respons sukses, kembalikan data response
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update user")
        } else {
            throw Exception("Failed to update user: ${response.message()}")
        }
    }


    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        val loginData = response.data
        if (loginData != null && !loginData.token.isNullOrEmpty()) {
            val userModel = UserModel(
                email,
                loginData.token,
                true,
                loginData.name ?: "",
                loginData.id ?: ""
            )
            saveSession(userModel)
        }
        return response
    }


    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}