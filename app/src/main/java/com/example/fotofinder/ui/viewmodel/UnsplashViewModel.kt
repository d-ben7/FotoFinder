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

@HiltViewModel
class UnsplashViewModel @Inject constructor(private val repository: UnsplashRepository) : ViewModel() {

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
        fetchPhotos(SearchOptions.TOP_PICKS)
    }

    /**
     * Fetch photos from repo
     * @param searchOption - type of endpoints
     * @param query - optional query (eg. user id, topic id, or search terms)
     */
    fun fetchPhotos(searchOption: SearchOptions, query: String? = null) {
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
     * Update current selected topic chip
     * @param chipName - name of the chip
     */
    fun setActiveChipKey(chipName: String?) {
        _activeTopicChipKey.value = chipName
    }

}