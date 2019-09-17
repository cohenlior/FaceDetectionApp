package com.lior.facedetectionapp.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.vision.Frame
import com.lior.facedetectionapp.domain.ImageGallery
import com.lior.facedetectionapp.domain.asDomainModel
import com.lior.facedetectionapp.utils.PRIVATE_DIRECTORY
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.coroutines.*


class FaceDetectionRepository(private val context: Context) {

    private var composite = CompositeDisposable()

    private val job = Job()

    private val viewModelScope = CoroutineScope(job + Dispatchers.Main)

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

        val list = imageGalleryList.value
        if (list == null || list.isEmpty()) {
            return
        }

        val detector: com.google.android.gms.vision.face.FaceDetector =
            com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                .build()

        viewModelScope.launch(Dispatchers.Default) {
            detectImages(
                Picasso.get(),
                list,
                detector,
                Frame.Builder()
            ) { facesList, noFacesList, index, completed ->
                withContext(Dispatchers.Main) {
                    updateResults(facesList, noFacesList, index, completed)
                }
            }
        }
    }

    private fun updateResults(
        facesList: List<ImageGallery>,
        noFacesList: List<ImageGallery>,
        index: Int, completed: Boolean
    ) {
        _result.value = Pair(index, facesList.size)
        if (completed) {
            _facesPictures.value = facesList
            _noFacesPictures.value = noFacesList
        }
    }

    fun onClear() {
        composite.clear()
        job.cancel()
    }
}




