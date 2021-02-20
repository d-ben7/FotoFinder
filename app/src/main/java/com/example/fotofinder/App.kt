package com.example.fotofinder

import android.app.Application
import com.example.fotofinder.util.NetworkStatusHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        private lateinit var instance: Application
        fun getContext() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        NetworkStatusHelper.init(this)
    }
}