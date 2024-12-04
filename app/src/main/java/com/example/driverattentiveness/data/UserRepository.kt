package com.example.driverattentiveness.data

import com.example.driverattentiveness.data.api.response.LoginResponse
import com.example.driverattentiveness.data.api.retrofit.ApiService
import com.example.driverattentiveness.data.pref.UserModel
import com.example.driverattentiveness.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

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
    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        val loginData = response.data
        if (loginData != null && !loginData.token.isNullOrEmpty()) {
            val userModel = UserModel(email, loginData.token, true)
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