package com.lior.facedetectionapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lior.facedetectionapp.R
import com.lior.facedetectionapp.databinding.FragmentBottomNavFragmentAllBinding
import com.lior.facedetectionapp.databinding.FragmentBottomNavFragmentFacesBinding
import com.lior.facedetectionapp.ui.adapter.ImageListAdapter
import com.lior.facedetectionapp.ui.viewmodel.FaceDetectionViewModel
import com.lior.facedetectionapp.ui.viewmodel.FaceDetectionViewModelFactory

class BottomNavFragmentFaces : Fragment() {

    private lateinit var model: FaceDetectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentBottomNavFragmentFacesBinding.inflate(inflater)

        model = activity?.run {
            ViewModelProviders.of(this)[FaceDetectionViewModel::class.java]
        }?: throw Exception("Invalid Activity")

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = model

        binding.imageListFaces.adapter = ImageListAdapter()

        return binding.root
    }
}

