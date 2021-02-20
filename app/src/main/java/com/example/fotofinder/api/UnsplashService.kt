package com.example.fotofinder.api

import com.example.fotofinder.BuildConfig
import com.example.fotofinder.model.TopicResponse
import com.example.fotofinder.model.UnsplashResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashService {

    companion object {
        // Unsplash access key
        private const val CLIENT_ID = BuildConfig.API_KEY
    }

    /**
     * Search photos using search terms
     */
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos")
    suspend fun getPhotosByTags(
        @Query("query") tags: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): UnsplashResponse

    /**
     * Search the top pick photos (eg. home page photos)
     */
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("/photos")
    suspend fun getPhotosByTopPicks(
            @Query("page") page: Int,
            @Query("per_page") perPage: Int,
    ): List<UnsplashResponse.Photo>

    /**
     * Search photos based on a category/topic
     */
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("/topics/{id}/photos")
    suspend fun getPhotosByTopic(
        @Path("id")  topicId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<UnsplashResponse.Photo>

    /**
     * Get a list of available topics/categories
     */
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("/topics")
    suspend fun getTopics(
        @Query("per_page") perPage: Int,
    ): List<TopicResponse>

    /**
     * Search photos by a user
     */
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("/users/{username}/photos")
    suspend fun getPhotosByUser(
        @Path("username")  username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<UnsplashResponse.Photo>

}