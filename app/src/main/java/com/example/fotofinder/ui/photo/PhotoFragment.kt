package com.example.fotofinder.ui.photo

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.showSystemAppDetailsPage
import com.example.fotofinder.R
import com.example.fotofinder.databinding.FragmentPhotoBinding
import com.example.fotofinder.util.NetworkStatusHelper
import com.example.fotofinder.model.UnsplashResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!

    private val args: PhotoFragmentArgs by navArgs()
    private lateinit var photo: UnsplashResponse.Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set shared element anim
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get the selected photo from safe args
        photo = args.unsplashPhoto
        setupUi(photo)
    }

    /**
     * Bind the views and setup button click listeners
     */
    private fun setupUi(photo: UnsplashResponse.Photo) {
        binding.ivPhoto.apply {
            transitionName = "photoTransition" // for shared element anim
            load(photo.urls.regular) {
                crossfade(true)
            }
        }

        binding.ivProfileImage.load(photo.user.profile_image.large) {
            transformations(CircleCropTransformation())
        }

        binding.username.text = photo.user.name

        binding.btnDownload.setOnClickListener {
            if(NetworkStatusHelper.isNetworkAvailable()) {
                handleDownloadRequest()
            } else {
                Toasty.error(requireContext(), "Connection error!", Toasty.LENGTH_SHORT, true).show()
            }
        }

        binding.ivProfileImage.setOnClickListener {
            it.transitionName = "profileImageTransition" // for shared element anim
            val extras = FragmentNavigatorExtras(it to "profileImageTransition")
            val action =
                PhotoFragmentDirections.actionPhotoFragmentToUserFragment(photo.user)
            Navigation.findNavController(it).navigate(action, extras)
        }
    }

    /**
     * Handle the download request
     * Check or request storage permission. If granted, proceed to create bitmap, otherwise display error message
     */
    private fun handleDownloadRequest() {
        askForPermissions(Permission.WRITE_EXTERNAL_STORAGE) { result ->
            when (result[Permission.WRITE_EXTERNAL_STORAGE]) {
                GrantResult.GRANTED -> createBitmap()
                GrantResult.PERMANENTLY_DENIED -> onPermissionPermanentlyDenied()
                else -> Toasty.info(requireContext(), "Permission denied. Download failed.", Toasty.LENGTH_LONG, true).show()
            }
        }
    }

    /**
     * Create bitmap from image url. If successful, proceed to save bitmap to device storage
     */
    private fun createBitmap() {
        binding.downloadProgress.isVisible = true
        lifecycleScope.launch {
            val fileName = "Unsplash-${photo.id}.jpg"
            val imageLoader = requireContext().imageLoader
            val request = ImageRequest.Builder(requireContext()).data(photo.urls.full).build()
            val bitmap = imageLoader.execute(request).drawable?.toBitmap()
            if (bitmap != null) {
                saveImage(bitmap, fileName)
            }
            withContext(Dispatchers.Main) {
                binding.downloadProgress.isVisible = false
            }
        }
    }

    /**
     * Display dialog permission rationale before navigating to App Info page
     */
    private fun onPermissionPermanentlyDenied() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.storagePermissionDialogTitle))
            .setMessage(resources.getString(R.string.storagePermissionDialogMessage))
            .setPositiveButton("Close", null)
            .setOnDismissListener {
                showSystemAppDetailsPage()
            }
            .show()
    }

    /**
     * Save photo to device storage in the Download directory
     * @param bitmap the photo to download
     * @param fileName name of downloaded jpeg
     */
    private fun saveImage(bitmap: Bitmap, fileName: String) {
        // Get the external storage directory path
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
         // Create a file to save the image
        val file = File(path, fileName)

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)
            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // Flush the output stream
            stream.flush()
            // Close the output stream
            stream.close()
            Toasty.success(requireContext(), "Download completed", Toasty.LENGTH_SHORT, true).show()
        } catch (e: IOException){ // Catch the exception
            Toasty.error(requireContext(), "Download failed.", Toasty.LENGTH_SHORT, true).show()
        }

    }

    override fun onDestroyView() {
        _binding = null // set to null to avoid leaks
        super.onDestroyView()
    }


}