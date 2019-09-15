package com.lior.facedetectionapp.ui.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.lior.facedetectionapp.R
import com.lior.facedetectionapp.databinding.FragmentBottomNavFragmentAllBinding
import com.lior.facedetectionapp.ui.adapter.ImageListAdapter
import com.lior.facedetectionapp.ui.viewmodel.FaceDetectionViewModel

private const val STORAGE_PERMISSION_REQUEST = 1

private const val STORAGE_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE"

class BottomNavFragmentAll : Fragment() {

    private lateinit var viewModel: FaceDetectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = this.activity?.run {
            ViewModelProviders.of(this)[FaceDetectionViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        requestStoragePermissionOrStartLoadingImages()
    }

    private fun requestStoragePermissionOrStartLoadingImages() {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                STORAGE_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestStoragePermission()
            return
        }

        viewModel.startLoadingImages()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBottomNavFragmentAllBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.imageList.adapter = ImageListAdapter()

        return binding.root
    }

    /**
     * Show the user a dialog asking for permission to use location.
     */
    private fun requestStoragePermission() {
        requestPermissions(arrayOf(STORAGE_PERMISSION), STORAGE_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startLoadingImages()
                } else {
                    Toast.makeText(
                        activity, getString(R.string.require_storage_permission),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }
}