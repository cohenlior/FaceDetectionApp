package com.lior.facedetectionapp.repository

import android.graphics.Bitmap
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.lior.facedetectionapp.domain.ImageGallery
import com.squareup.picasso.Picasso
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun detectImagesConcurrent(
    picasso: Picasso,
    imageGalleryList: List<ImageGallery>,
    faceDetector: FaceDetector,
    frame: Frame.Builder,
    updateResults: suspend (facesList: List<ImageGallery>, noFacesList: List<ImageGallery>, index: Int, completed: Boolean) -> Unit
) = coroutineScope {

    val channel = Channel<ImageGallery>()
    for (image in imageGalleryList) {
        launch {
            val bitmap: Bitmap = picasso.load(image.uri).get()
            val faces = faceDetector.detect(frame.setBitmap(bitmap).build())
            image.hasFace = faces.size() >= 1
            channel.send(image)
        }
    }
    val facesImageList = mutableListOf<ImageGallery>()
    val noFacesImageList = mutableListOf<ImageGallery>()
    repeat(imageGalleryList.size) {
        val image = channel.receive()

        when(image.hasFace){
            true -> facesImageList.add(image)
            else -> noFacesImageList.add(image)
        }

        updateResults(facesImageList, noFacesImageList, it + 1, it == imageGalleryList.size - 1)
    }
}

