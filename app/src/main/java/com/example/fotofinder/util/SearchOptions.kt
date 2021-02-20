package com.example.fotofinder.util

/**
 * Type for end points of the Unsplash api
 */
sealed class SearchOptions {
    object TAG: SearchOptions()
    object TOPIC: SearchOptions()
    object TOP_PICKS: SearchOptions()
    object USER: SearchOptions()
}