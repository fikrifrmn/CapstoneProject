package com.example.driverattentiveness.data

<<<<<<< HEAD
import com.example.driverattentiveness.data.api.response.LoginResponse
import com.example.driverattentiveness.data.api.retrofit.ApiService
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
import com.example.driverattentiveness.data.pref.UserModel
import com.example.driverattentiveness.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
<<<<<<< HEAD
    private val userPreference: UserPreference,
    private val apiService: ApiService
=======
    private val userPreference: UserPreference
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }
<<<<<<< HEAD
    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        val loginData = response.data
        if (loginData != null && !loginData.token.isNullOrEmpty()) {
            val userModel = UserModel(email, loginData.token, true)
            saveSession(userModel)
        }
        return response
    }
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
<<<<<<< HEAD
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
=======
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
            }.also { instance = it }
    }
}