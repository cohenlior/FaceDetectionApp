package com.lior.facedetectionapp.domain

import android.net.Uri
import java.io.File

var imagePattern = "(.png)|(.jpg)|(.gif)".toRegex(RegexOption.IGNORE_CASE)

data class ImageGallery(
    val name: String,
    val uri: Uri,
    var hasFace: Boolean
)

fun List<File>.asDomainModel(): List<ImageGallery> {
    return map {
        ImageGallery(
            name = it.name,
            uri = Uri.fromFile(File(it.absolutePath)),
            hasFace = false
        )
    }.filter {
        it.name.contains(imagePattern)
    }
}