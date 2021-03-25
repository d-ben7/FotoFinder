package com.example.fotofinder.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import com.example.fotofinder.R
import com.example.fotofinder.model.UnsplashResponse
import com.example.fotofinder.databinding.FragmentGalleryBinding
import com.example.fotofinder.ui.viewmodel.UnsplashViewModel
import com.example.fotofinder.ui.adapter.PhotoAdapter
import com.example.fotofinder.util.SearchOptions
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(), PhotoAdapter.OnItemClickListener {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val adapter = PhotoAdapter(this)
    private val unsplashViewModel: UnsplashViewModel by activityViewModels()

    private val unsplashTopics = mutableMapOf<String, String>()
    private lateinit var defaultTopic: String
    private var activeChipKey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        defaultTopic = requireContext().resources.getString(R.string.defaultTopic)
        unsplashTopics["Unsplash Picks"] = defaultTopic // Set the first topic chip (eg. home page photos)
        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    /**
     * Observe view model live data
     */
    private fun setupObservers() {
        unsplashViewModel.mainGalleryPhotos.observe(viewLifecycleOwner, { photos ->
            adapter.submitData(viewLifecycleOwner.lifecycle, photos)
        })

        unsplashViewModel.activeTopicChipKey.observe(viewLifecycleOwner, { chipKey ->
            activeChipKey = chipKey
        })

        unsplashViewModel.topics.observe(viewLifecycleOwner, { topics ->
            unsplashTopics.putAll(topics)
            setupTopicChips(unsplashTopics)
        })
    }

    /**
     * Set up the search bar
     */
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.topicChipGroup.clearCheck()
                unsplashViewModel.setActiveChipKey(null)
                unsplashViewModel.fetchPhotos(SearchOptions.SEARCH_TERMS, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean { return false }
        })
    }

    // Set up the topic chips
    private fun setupTopicChips(topics: Map<String, String>) {
        // create chip for each topic
        topics.forEach { topic ->
            val chip = layoutInflater.inflate(R.layout.topic_chip, binding.topicChipGroup, false) as Chip
            chip.apply {
                id = View.generateViewId()
                text = topic.key // NOTE: topic.key is the topic title (eg. Nature), topic.value is the topic id use for api calls
                if (activeChipKey == topic.key) {
                    isChecked = true
                }
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        binding.searchView.setQuery("", false)
                        binding.searchView.clearFocus()
                        unsplashViewModel.setActiveChipKey(topic.key)
                        val topicId = topics[topic.key]
                        if (topicId == defaultTopic) {
                            unsplashViewModel.fetchPhotos(SearchOptions.TOP_PICKS)
                        } else {
                            unsplashViewModel.fetchPhotos(SearchOptions.TOPIC, topicId)
                        }
                    }
                }
            }

            binding.topicChipGroup.addView(chip)
        }

    }

    /**
     * Set up the recycler view for the main gallery
     */
    private fun setupRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS // recycler view will shuffle items to remove gaps
        }

        binding.galleryRecyclerView.layoutManager = layoutManager
        binding.galleryRecyclerView.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                loadingAnim.isVisible = loadState.source.refresh is LoadState.Loading
                errorMessage.isVisible = loadState.source.refresh is LoadState.Error
                noResultsMessage.isVisible = (loadState.source.refresh is LoadState.NotLoading
                        && loadState.append.endOfPaginationReached
                        && adapter.itemCount < 1)
            }
        }
    }


    override fun onDestroyView() {
        // set to null to avoid leaks
        binding.galleryRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }

    // Click listener for the gallery photo
    override fun onItemClicked(photo: UnsplashResponse.Photo, extras: FragmentNavigator.Extras) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToPhotoFragment(photo)
        findNavController().navigate(action, extras)
    }

}