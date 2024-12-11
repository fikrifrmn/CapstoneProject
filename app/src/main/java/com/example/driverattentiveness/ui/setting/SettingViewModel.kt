package com.example.driverattentiveness.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.pref.UserModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> get() = _user

    init {
        // Ambil data session pengguna
        viewModelScope.launch {
            userRepository.getSession().collect { user ->
                _user.postValue(user)
            }
        }
    }

    fun changePassword(newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Ambil token dan ID pengguna dari sesi saat ini
                val userSession = userRepository.getSession().first()
                val token = userSession.token
                val userId = userSession.id
                val userName = userSession.name  // Ambil nama pengguna

                // Pastikan token, userId, dan userName valid
                if (token.isNotEmpty() && userId.isNotEmpty() && userName.isNotEmpty()) {
                    val request = UserUpdateRequest(
                        name = userName,   // Sertakan nama pengguna
                        password = newPassword
                    )

                    val response = userRepository.updateUser(userId, request)

                    // Jika respons berhasil
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                // Tangani error, jika terjadi
                onResult(false)
            }
        }
    }
}
