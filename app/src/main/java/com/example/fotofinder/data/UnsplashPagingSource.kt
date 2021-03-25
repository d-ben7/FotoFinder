package com.example.fotofinder.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.fotofinder.model.UnsplashResponse
import com.example.fotofinder.api.UnsplashService
import com.example.fotofinder.data.UnsplashRepository.Companion.DEFAULT_PAGE_INDEX
import com.example.fotofinder.util.SearchOptions
import java.io.IOException
import retrofit2.HttpException

class UnsplashPagingSource(
    private val searchOption: SearchOptions,
    private val query: String?,
    private val unsplashService: UnsplashService) : PagingSource<Int, UnsplashResponse.Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashResponse.Photo> {
        val page = params.key ?: DEFAULT_PAGE_INDEX

        return try {
            var photos = listOf<UnsplashResponse.Photo>()

            // switch endpoints according to the search option
            when(searchOption) {
                SearchOptions.TOP_PICKS -> {
                    photos = unsplashService.getPhotosByTopPicks(page, params.loadSize)
                }
                SearchOptions.SEARCH_TERMS -> {
                    query?.let { tag ->
                        photos = unsplashService.getPhotosBySearchTerms(tag, page, params.loadSize).results
                    }
                }
                SearchOptions.TOPIC -> {
                    query?.let { topicId ->
                        photos = unsplashService.getPhotosByTopic(topicId, page, params.loadSize)
                    }
                }
                SearchOptions.USER -> {
                    query?.let { username ->
                        photos = unsplashService.getPhotosByUser(username, page, params.loadSize)
                    }
                }
            }

            LoadResult.Page(
                data = photos,
                prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashResponse.Photo>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

}