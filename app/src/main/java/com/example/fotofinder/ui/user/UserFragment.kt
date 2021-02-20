package com.example.fotofinder.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionInflater
import coil.load
import coil.transform.CircleCropTransformation
import com.example.fotofinder.R
import com.example.fotofinder.model.UnsplashResponse
import com.example.fotofinder.databinding.FragmentUserBinding
import com.example.fotofinder.ui.viewmodel.UnsplashViewModel
import com.example.fotofinder.ui.adapter.PhotoAdapter
import com.example.fotofinder.util.SearchOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment(), PhotoAdapter.OnItemClickListener {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val unsplashViewModel: UnsplashViewModel by activityViewModels()
    private val adapter = PhotoAdapter(this)

    private val args: UserFragmentArgs by navArgs()
    private lateinit var user: UnsplashResponse.Photo.User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set shared element anim
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        user = args.user
        // Fetch photos from the selected user here so it does not re-fetch onBackPressed
        unsplashViewModel.fetchPhotos(SearchOptions.USER, user.username)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi(user)
        setupRecyclerView()

        // Observe view model user photos
        unsplashViewModel.userGalleryPhotos.observe(viewLifecycleOwner, {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    /**
     * Set up the ui
     */
    private fun setupUi(user: UnsplashResponse.Photo.User) {
        binding.apply {
            userProfileImage.transitionName = "profileImageTransition"
            userProfileImage.load(user.profile_image.large) {
                transformations(CircleCropTransformation())
            }

            username.text = user.name

            if(!user.twitter_username.isNullOrEmpty()) {
                twitter.text = getString(R.string.socialMedia, user.twitter_username)
            } else {
                twitter.visibility = View.GONE
            }

            if(!user.instagram_username.isNullOrEmpty()) {
                instagram.text = getString(R.string.socialMedia, user.instagram_username)
            } else {
                instagram.visibility = View.GONE
            }

            if (!user.bio.isNullOrEmpty()) {
                bio.text = user.bio
            } else {
                bio.visibility = View.GONE
            }

            myPhotosLabel.text = getString(R.string.myPhotosLabel, user.total_photos)
        }
    }

    /**
     * Set up the recycle view for the user photos gallery
     */
    private fun setupRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS // recycler view will shuffle items to remove gaps
        }
        binding.userPhotosRV.layoutManager = layoutManager
        binding.userPhotosRV.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                loadingAnim.isVisible = loadState.source.refresh is LoadState.Loading
                userPhotosRV.isVisible = loadState.source.refresh is LoadState.NotLoading
                errorMessage.isVisible = loadState.source.refresh is LoadState.Error
            }
        }

    }


    // Click listener for the gallery photo
    override fun onItemClicked(photo: UnsplashResponse.Photo, extras: FragmentNavigator.Extras) {
        val action = UserFragmentDirections.actionUserFragmentToPhotoFragment(photo)
        findNavController().navigate(action, extras)
    }

    override fun onDestroyView() {
        // set to null to avoid leaks
        binding.userPhotosRV.adapter = null
        _binding = null
        super.onDestroyView()
    }

}