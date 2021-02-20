package com.example.fotofinder.model

/**
 * JSON response class for Unsplash topics
 * @param id - the unique topic id
 * @param title - title of the topic/category (eg. Nature, Technology...)
 */
data class TopicResponse(
    val id: String,
    val title: String,
)