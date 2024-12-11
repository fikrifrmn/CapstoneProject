package com.example.driverattentiveness.ui.camera

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.driverattentiveness.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    val detectionText = MutableLiveData<String>()
    val inferenceTimeText = MutableLiveData<String>()
    val boundingBoxes = MutableLiveData<List<BoundingBox>>()
    private val mediaPlayer = MediaPlayer.create(application, R.raw.beep_warning)

    fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            this@CameraViewModel.boundingBoxes.postValue(boundingBoxes)

            val message = when {
                boundingBoxes.isEmpty() -> "No Object"
                boundingBoxes.any { it.clsName == "alert" } -> "You are not driving safely"
                else -> "You are driving safely"
            }

            val alertDetected = boundingBoxes.any { it.clsName == "alert" }

            detectionText.postValue(message)
            inferenceTimeText.postValue("${inferenceTime}ms")

            withContext(Dispatchers.Main) {
                when {
                    boundingBoxes.isEmpty() -> {
                        if (mediaPlayer.isPlaying) mediaPlayer.pause()
                    }
                    alertDetected -> {
                        if (!mediaPlayer.isPlaying) mediaPlayer.start()
                    }
                    else -> {
                        if (mediaPlayer.isPlaying) mediaPlayer.pause()
                    }
                }
            }
        }
    }
    fun updatePredictionResult(prediction: String) {
        viewModelScope.launch(Dispatchers.Main) {
            detectionText.value = prediction
        }
    }

    fun onEmptyDetect() {
        viewModelScope.launch(Dispatchers.Main) {
            detectionText.value = "You are driving safely"
            mediaPlayer.takeIf { it.isPlaying }?.pause()
        }
    }
}