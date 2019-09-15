package com.lior.facedetectionapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lior.facedetectionapp.domain.ImageGallery
import com.lior.facedetectionapp.repository.FaceDetectionRepository

class FaceDetectionViewModel(private val repo: FaceDetectionRepository) : ViewModel() {

    private val _isDetectionAllowed = MutableLiveData<Boolean>()
    val isDetectionAllowed: LiveData<Boolean>
        get() = _isDetectionAllowed

    val allImages = repo.imageGalleryList
    val imagesWithFaces = repo.facesPictures
    val imagesWithNoFaces = repo.noFacesPictures

    fun startLoadingImages(){
        repo.loadImagesFromDevice()
    }
    fun onDetectClicked(){
        _isDetectionAllowed.value = allImages.value != null && allImages.value?.size  != 0

    }
}

