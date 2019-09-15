package com.lior.facedetectionapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.lior.facedetectionapp.R
import com.lior.facedetectionapp.databinding.FragmentBottomNavFragmentAllBinding
import com.lior.facedetectionapp.databinding.FragmentBottomNavFragmentNoFacesBinding
import com.lior.facedetectionapp.ui.adapter.ImageListAdapter
import com.lior.facedetectionapp.ui.viewmodel.FaceDetectionViewModel

class BottomNavFragmentNoFaces : Fragment() {

    private lateinit var model: FaceDetectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBottomNavFragmentNoFacesBinding.inflate(inflater)

        model = activity?.run {
            ViewModelProviders.of(this)[FaceDetectionViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = model

        binding.imageListNoFaces.adapter = ImageListAdapter()

        return binding.root
    }
}