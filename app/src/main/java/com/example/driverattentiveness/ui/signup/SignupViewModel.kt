package com.example.driverattentiveness.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.response.RegisterResponse
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.pref.UserModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String,age:String, onSuccess: (RegisterResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token = email)
                val response = apiService.register(name, email, password,age)
                val user = UserModel(email, password, isLogin = true, name = name)
                userRepository.saveSession(user)

                onSuccess(response)

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                onError(errorBody ?: "An unknown error occurred")
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }
}