package com.lior.facedetectionapp.repository

import android.graphics.Bitmap
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.lior.facedetectionapp.domain.ImageGallery
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

suspend fun detectImages(
    picasso: Picasso,
    imageGalleryList: List<ImageGallery>,
    faceDetector: FaceDetector,
    frame: Frame.Builder,
    updateResults: suspend (facesList: List<ImageGallery>,noFacesList: List<ImageGallery>, index: Int, completed: Boolean) -> Unit
) {
    val facesImageList = mutableListOf<ImageGallery>()
    val noFacesImageList = mutableListOf<ImageGallery>()
    withContext(Dispatchers.IO) {
        imageGalleryList.forEachIndexed { index, image ->
            val bitmap: Bitmap = picasso.load(image.uri).get()
            val faces = faceDetector.detect(frame.setBitmap(bitmap).build())

            if (faces.size() >= 1) facesImageList.add(image) else noFacesImageList.add(image)

            updateResults(facesImageList, noFacesImageList,  index+1, index == imageGalleryList.size-1)
        }
    }
}

