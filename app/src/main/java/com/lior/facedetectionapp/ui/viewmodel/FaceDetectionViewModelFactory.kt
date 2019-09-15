package com.lior.facedetectionapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lior.facedetectionapp.repository.FaceDetectionRepository

class FaceDetectionViewModelFactory(
    private val facesDetectionRepository: FaceDetectionRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FaceDetectionViewModel::class.java)) {
            return FaceDetectionViewModel(facesDetectionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}