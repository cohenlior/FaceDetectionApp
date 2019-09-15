package com.lior.facedetectionapp.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.lior.facedetectionapp.domain.ImageGallery
import com.lior.facedetectionapp.domain.asDomainModel
import com.lior.facedetectionapp.utils.ImageDetectorRx
import com.lior.facedetectionapp.utils.PRIVATE_DIRECTORY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class FaceDetectionRepository(private val context: Context) {

    private var composite = CompositeDisposable()

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: FaceDetectionRepository? = null

        fun getInstance(context: Context): FaceDetectionRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FaceDetectionRepository(context.applicationContext).also { INSTANCE = it }
            }
    }

    private val _imageGalleryList = MutableLiveData<List<ImageGallery>>()
    val imageGalleryList: LiveData<List<ImageGallery>>
        get() = _imageGalleryList

    private val _facesPictures = MutableLiveData<List<ImageGallery>>()
    val facesPictures: LiveData<List<ImageGallery>>
        get() = _facesPictures

    private val _noFacesPictures = MutableLiveData<List<ImageGallery>>()
    val noFacesPictures: LiveData<List<ImageGallery>>
        get() = _noFacesPictures

    private val _result = MutableLiveData<Pair<Int, Int>>()
    val result: LiveData<Pair<Int, Int>>
        get() = _result


    fun loadImagesFromDevice() {
        composite.add(Observable.fromCallable { loadImagesInBackground() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> onLoadSuccess(result) },
                { onLoadError() })
        )
    }

    private fun loadImagesInBackground(): List<ImageGallery> {
        val directory =
            Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOWNLOADS}$PRIVATE_DIRECTORY")
        val files = directory.listFiles()
        return files?.toList()?.asDomainModel() ?: listOf()
    }

    private fun onLoadSuccess(result: List<ImageGallery>?) {
        _imageGalleryList.value = result
    }

    private fun onLoadError() {
        Log.d("FaceDetectionRepository", "Load images failed")
    }

    fun searchFacesProcess() {
        Log.d("FaceDetectionService", "Search faces process started")

        val size = imageGalleryList.value!!.size

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .build()
        val fireBaseDetector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)


        val facesList = mutableListOf<ImageGallery>()
        val noFacesList = mutableListOf<ImageGallery>()
        val rxImageDetector = ImageDetectorRx(context, fireBaseDetector, size)

        composite.add(
            rxImageDetector.facesSubject
                .subscribeOn(Schedulers.io())
                .subscribeBy(onNext = { imageGallery ->
                    if (imageGallery.hasFaces) {
                        facesList.add(imageGallery)
                    } else {
                        noFacesList.add(imageGallery)
                    }
                    _result.postValue(Pair(facesList.size.plus(noFacesList.size), facesList.size))

                }, onComplete = { onComplete(facesList, noFacesList) })
        )

        composite.add(Observable.fromCallable { detectInBackground(rxImageDetector) }
            .subscribeOn(Schedulers.io())
            .subscribe()
        )
    }

    private fun detectInBackground(rxImageDetector: ImageDetectorRx) {
        imageGalleryList.value?.forEach {
            rxImageDetector.detectFaces(it)
        }
    }

    private fun onComplete(
        facesList: MutableList<ImageGallery>,
        noFacesList: MutableList<ImageGallery>
    ) {
        _facesPictures.postValue(facesList)
        _noFacesPictures.postValue(noFacesList)
    }

    fun onClear() {
        composite.clear()
    }
}




