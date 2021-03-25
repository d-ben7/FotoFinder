package com.example.fotofinder.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.fotofinder.model.UnsplashResponse
import com.example.fotofinder.api.UnsplashService
import com.example.fotofinder.model.TopicResponse
import com.example.fotofinder.util.SearchOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashService: UnsplashService) {

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
        const val DEFAULT_PAGE_SIZE = 30
    }

    /**
     * Fetch Unsplash photos from network
     * @param searchOption - type of endpoints
     * @param query - optional query (eg. user id, topic id, or search terms)
     */
    fun getUnsplashPhotos(searchOption: SearchOptions, query: String?): LiveData<PagingData<UnsplashResponse.Photo>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { UnsplashPagingSource(searchOption, query, unsplashService) }
        ).liveData
    }

    /**
     * Fetch available topics/categories on Unsplash
     */
    suspend fun getUnsplashTopics(): List<TopicResponse> {
        return unsplashService.getTopics(DEFAULT_PAGE_SIZE)
    }

}