package com.syukron.nutriary.imagesearch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.syukron.nutriary.databinding.ActivityImageSearchBinding
import com.syukron.nutriary.databinding.FragmentSearchBinding
import com.syukron.nutriary.ui.fragment.SearchFragment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageSearchBinding
    private lateinit var viewModel: ImageSearchViewModel

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>


    private var currentPhotoPath: String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ImageSearchViewModel::class.java)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoFile = File(currentPhotoPath)
                displayImage(photoFile)
                viewModel.uploadImage(photoFile)
            }
        }



        // Inisialisasi launcher untuk galeri
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val imageFile = createImageFile()
            val outputStream = FileOutputStream(imageFile)

            if (uri != null) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            outputStream.close()

            if (imageFile != null) {
                displayImage(imageFile)
            }
            if (imageFile != null) {
                viewModel.uploadImage(imageFile)
            }
        }

        viewModel.predictionResult.observe(this, Observer { prediction ->
            binding.tvResult.text = prediction
        })


        binding.postButton.setOnClickListener {
            val prediction = binding.tvResult.text.toString()
            val searchFragment = SearchFragment()
            val bundle = Bundle()
            bundle.putString("searchQuery", prediction)
            searchFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, searchFragment)
                .commit()

        }

        binding.btCamera.setOnClickListener {
            openCamera()

        }

        binding.btGallery.setOnClickListener {
            openGallery()
        }

    }



    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
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
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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


}