package com.syukron.nutriary.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment

import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.syukron.nutriary.R
import com.syukron.nutriary.databinding.FragmentCameraBinding
import com.syukron.nutriary.databinding.FragmentSearchBinding
import com.syukron.nutriary.imagesearch.ImageSearchViewModel
import com.syukron.nutriary.ui.TrackerViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CameraFragment :
    BaseFragment<FragmentCameraBinding>(
        FragmentCameraBinding::inflate,
        lockDrawer = true,
        hasOptionsMenu = true
    ) {


    private var _binding: FragmentCameraBinding? = null

    private lateinit var viewModel: ImageSearchViewModel





    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var currentPhotoPath: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImageSearchViewModel::class.java)


        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoFile = File(currentPhotoPath)
                displayImage(photoFile)
                viewModel.uploadImage(photoFile)
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val imageFile = createImageFile()
            val outputStream = FileOutputStream(imageFile)

            if (uri != null) {
                requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            outputStream.close()

            if (imageFile != null) {
                displayImage(imageFile)
                viewModel.uploadImage(imageFile)
            }
        }


        viewModel.predictionResult.observe(viewLifecycleOwner, Observer { prediction ->
            binding.tvResult.text = prediction
        })

        binding.postButton.setOnClickListener {
            val query = binding.tvResult.text.toString()
            lifecycleScope.launch {
                try {
                    sharedViewModel.searchFoodsWithQuery(query)
                    this@CameraFragment.findNavController()
                        .navigate(R.id.action_cameraFragment_to_trackerFragment)
                } catch (e: Exception) {
                    com.google.android.material.snackbar.Snackbar.make(
                        requireView(),
                        e.toString(),
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).show()
                    Log.d("TAG", "Terjadi kesalahan: " + Log.getStackTraceString(e))

                }
            }
        }

        binding.btCamera.setOnClickListener {
            openCamera()
        }

        binding.btGallery.setOnClickListener {
            openGallery()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.ukonkeren.nutriary.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(intent)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun displayImage(imageFile: File) {
        Glide.with(this)
            .load(imageFile)
            .centerCrop()
            .into(binding.ivPreviewImage)
    }

    private fun navigateToSearch() {
        sharedViewModel.predictionResult.value?.let { prediction ->
            val argument = bundleOf(
                "upButtonNeeded" to true,
                "searchQuery" to prediction
            )
            this@CameraFragment
                .findNavController()
                .navigate(
                    R.id.action_cameraFragment_to_searchFragment,
                    argument
                )
        }
    }

}