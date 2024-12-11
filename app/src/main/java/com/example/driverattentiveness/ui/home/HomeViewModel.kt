package com.example.driverattentiveness.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverattentiveness.BuildConfig
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.response.ArticleResponse
import com.example.driverattentiveness.data.api.response.ArticlesItem
import com.example.driverattentiveness.data.api.retrofit.ApiArticleConfig
import com.example.driverattentiveness.data.pref.UserModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val apiKey: String = BuildConfig.API_KEY

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> = _user

    private val _articles = MutableLiveData<List<ArticlesItem?>>()
    val articles: LiveData<List<ArticlesItem?>> = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val user = userRepository.getSession().collect { result ->
                _user.postValue(result)
            }
        }
    }

    fun fetchArticles() {
        _isLoading.value = true
        Log.d(TAG, "API Key: $apiKey")
        ApiArticleConfig.getApiService().getData(apiKey = apiKey)
            .enqueue(object : Callback<ArticleResponse> {
                override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val validArticles = responseBody?.articles?.filter {
                            it?.title != "[Removed]" && it?.description != "[Removed]" && it?.content != "[Removed]"
                        }
                        _articles.value = validArticles ?: emptyList()
                    } else {
                        Log.e(TAG, "onResponse failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
