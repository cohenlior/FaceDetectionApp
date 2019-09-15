package com.lior.facedetectionapp.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lior.facedetectionapp.domain.ImageGallery
import com.lior.facedetectionapp.ui.adapter.ImageListAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageList")
fun bindRecyclerViewImages(recyclerView: RecyclerView, list: List<ImageGallery>?) {
    val adapter = recyclerView.adapter as ImageListAdapter
    adapter.submitList(list)
}

@BindingAdapter("imageFile")
fun bindImage(imgView: ImageView, imgUri: Uri?) {
    imgUri?.let {
        Picasso.get()
            .load(imgUri)
            .centerCrop()
            .fit()
            .into(imgView)
    }
}