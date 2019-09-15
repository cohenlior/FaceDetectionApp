package com.lior.facedetectionapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lior.facedetectionapp.databinding.ListItemImageBinding
import com.lior.facedetectionapp.domain.ImageGallery

class ImageListAdapter :
    ListAdapter<ImageGallery, ImageListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }

    class ViewHolder private constructor(val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageGallery) {
            binding.imageGallery = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListItemImageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ImageGallery>() {
        override fun areItemsTheSame(oldItem: ImageGallery, newItem: ImageGallery): Boolean {
            return oldItem.name === newItem.name
        }

        override fun areContentsTheSame(oldItem: ImageGallery, newItem: ImageGallery): Boolean {
            return oldItem == newItem
        }
    }
}