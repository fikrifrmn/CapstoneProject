package com.example.driverattentiveness.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverattentiveness.data.UserRepository
<<<<<<< HEAD
import com.example.driverattentiveness.data.api.response.LoginResponse
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
import com.example.driverattentiveness.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

<<<<<<< HEAD
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)

                val loginData = response.data
                if (loginData != null && !loginData.token.isNullOrEmpty()) {
                    // Save user session
                    val user = UserModel(
                        email = email,
                        token = loginData.token,
                        isLogin = true
                    )
                    repository.saveSession(user)

                    // Update LiveData
                    _loginResponse.postValue(response)
                } else {
                    // Error: No valid data or token
                    _errorMessage.postValue(response.message ?: "Login failed: No data available")
                }
            } catch (e: Exception) {
                // Handle exception
                _errorMessage.postValue(e.message ?: "An unexpected error occurred")
            }
=======
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
        }
    }
}