package com.example.fotofinder.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * JSON response class for list of Unsplash photos
 * @param results - list of the photos json response
 */
@Parcelize
class UnsplashResponse(
    val results: List<Photo>
) : Parcelable {

    /**
     * Unsplash photos
     */
    @Parcelize
    data class Photo(
        val id: String,
        val width: Int,
        val height: Int,
        val urls: Urls,
        val user: User,
    ) : Parcelable {

        /**
         * Image urls
         */
        @Parcelize
        data class Urls(
            val full: String,
            val regular: String,
            val small: String,
        ) : Parcelable

        /**
         * User of the photo
         */
        @Parcelize
        data class User(
            val id: String,
            val username: String,
            val name: String,
            val twitter_username: String?,
            val bio: String?,
            val profile_image: ProfileImage,
            val instagram_username: String?,
            val total_photos: Int,
        ) : Parcelable {

            /**
             * User profile image
             */
            @Parcelize
            data class ProfileImage(
                val large: String
            ) : Parcelable
        }
    }
}