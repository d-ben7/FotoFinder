package com.example.fotofinder.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fotofinder.App
import com.example.fotofinder.R
import com.example.fotofinder.data.UnsplashRepository
import com.example.fotofinder.util.NetworkStatusHelper
import com.example.fotofinder.model.UnsplashResponse
import com.example.fotofinder.util.SearchOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Keys for the SaveStateHandle
private const val SEARCH_OPTION_KEY = "SEARCH_OPTION_KEY"
private const val QUERY_KEY = "QUERY_KEY"
private const val USER_ID_KEY = "USER_ID_KEY"

@HiltViewModel
class UnsplashViewModel @Inject constructor(
    private val repository: UnsplashRepository,
    private val saveStateHandle: SavedStateHandle
    ) : ViewModel() {

    // photos for the main gallery
    private val _mainGalleryPhotos = MediatorLiveData<PagingData<UnsplashResponse.Photo>>()
    val mainGalleryPhotos: LiveData<PagingData<UnsplashResponse.Photo>> = _mainGalleryPhotos.distinctUntilChanged()

    // photos for the user gallery
    private val _userGalleryPhotos = MediatorLiveData<PagingData<UnsplashResponse.Photo>>()
    val userGalleryPhotos: LiveData<PagingData<UnsplashResponse.Photo>> = _userGalleryPhotos.distinctUntilChanged()

    // list of the current photo topics/categories
    val topics: LiveData<MutableMap<String, String>> = liveData {
        if (NetworkStatusHelper.isNetworkAvailable()) {
            val response = repository.getUnsplashTopics()
            val topics = mutableMapOf<String, String>()
            response.forEach {
                topics[it.title] = it.id
            }
            emit(topics)
        }

    }

    // current selected topic ship
    private val _activeTopicChipKey = MutableLiveData<String?>()
    val activeTopicChipKey: LiveData<String?> = _activeTopicChipKey.distinctUntilChanged()

    init {
        _activeTopicChipKey.value = App.getContext().resources.getString(R.string.defaultTopic)

        // If process death occurs, then retrieve the last SearchOption and query to fetch photos
        // and restore the main gallery. Otherwise fetch the TOP PICKS photos for the main gallery
        val mainGallerySearchOption = saveStateHandle.get<SearchOptions>(SEARCH_OPTION_KEY) ?: SearchOptions.TOP_PICKS
        val mainGalleryQuery = saveStateHandle.get<String>(QUERY_KEY) // contains search term or null
        fetchPhotos(mainGallerySearchOption, mainGalleryQuery)

        // If process death occurs when user gallery was populated, then retrieve that user's ID to
        // fetch the user photos and restore the user gallery. Otherwise, do nothing
        val userId = saveStateHandle.get<String>(USER_ID_KEY)
        userId?.let { id ->
            fetchPhotos(SearchOptions.USER, id)
        }

    }

    /**
     * Fetch photos from repo
     * @param searchOption - type of endpoints
     * @param query - optional query (eg. user id, topic id, or search terms)
     */
    fun fetchPhotos(searchOption: SearchOptions, query: String? = null) {
        updateSaveStateHandle(searchOption, query)

        // check network connection first
        if (NetworkStatusHelper.isNetworkAvailable()) {
            val data = repository.getUnsplashPhotos(searchOption, query)
                .cachedIn(viewModelScope)

            // update gallery photos
            if (searchOption == SearchOptions.USER) {
                _userGalleryPhotos.addSource(data) { photos ->
                    _userGalleryPhotos.value = photos
                }
            } else {
                _mainGalleryPhotos.addSource(data) { photos ->
                    _mainGalleryPhotos.value = photos
                }
            }
        }

    }

    /**
     * Update the keys in SaveStateHandle for process death handling
     * @param searchOption The latest SearchOption
     * @param query The optional search terms or the user's ID if searchOption is SearchOption.USER
     */
    private fun updateSaveStateHandle(searchOption: SearchOptions, query: String?) {
        if (searchOption == SearchOptions.USER) {
            // Save the latest user ID
            saveStateHandle[USER_ID_KEY] = query // user ID
        } else {
            // Save the latest SearchOption and query
            saveStateHandle[SEARCH_OPTION_KEY] = searchOption
            saveStateHandle[QUERY_KEY] = query // contain search terms if not null
        }
    }

    /**
     * Update current selected topic chip
     * @param chipName - name of the chip
     */
    fun setActiveChipKey(chipName: String?) {
        _activeTopicChipKey.value = chipName
    }

}