package com.lior.facedetectionapp.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.lior.facedetectionapp.domain.ImageGallery
import io.reactivex.subjects.PublishSubject

class ImageDetectorRx(
    private val context: Context,
    private val fireBaseDetector: FirebaseVisionFaceDetector,
    private val listCounter: Int
) {

    private var counter: Int = 0;
    internal val facesSubject = PublishSubject.create<ImageGallery>()

    fun detectFaces(imageGallery: ImageGallery) {
        fireBaseDetector.detectInImage(imageFromPath(context, imageGallery.uri)).addOnSuccessListener { faces ->
            imageGallery.hasFaces = faces.size >= 1
            facesSubject.onNext(imageGallery)
            counter++
            if (counter >= listCounter) {
                facesSubject.onComplete()
            }
        }.addOnFailureListener {
            imageGallery.hasFaces = false
            facesSubject.onNext(imageGallery)
            counter++
            if (counter >= listCounter) {
                facesSubject.onComplete()
            }
        }
    }

    private fun imageFromPath(context: Context, uri: Uri) = FirebaseVisionImage.fromFilePath(context, uri)

/*    fun detectFacesSubject(imageGallery: ImageGallery): Observable<ImageGallery> {
        val facesSubjectObservable = PublishSubject.create<ImageGallery>()
        fireBaseDetector.detectInImage(imageFromPath(context, imageGallery.uri)).addOnSuccessListener { faces ->
            imageGallery.hasFaces = faces.size >= 1
            facesSubjectObservable.onNext(imageGallery)
        }.addOnFailureListener {
            imageGallery.hasFaces = false
            facesSubjectObservable.onNext(imageGallery)
        }
        return facesSubjectObservable
    }*/
}