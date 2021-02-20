package com.example.fotofinder.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Provide an observable of the network status and a method to check for connectivity
 * READ:
 * Limitation -> even though a network is shown connected, it may not always have internet access
 * Possible solution -> we can periodically ping Google to check we have internet communication on the active network
 */
object NetworkStatusHelper : LiveData<Boolean>() {
    private lateinit var application: Application
    private lateinit var networkRequest: NetworkRequest
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    fun init(application: Application) {
        NetworkStatusHelper.application = application
        connectivityManager = NetworkStatusHelper.application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        networkCallback = createNetworkCallback()
    }

    override fun onActive() {
        super.onActive()

        // Check for internet on startup
        // This is needed to show network error dialog for a scenario where user launch app without wifi and cellular,
        // since network callback does not detect no connections on registered
        CoroutineScope(Dispatchers.IO).launch {
            val hasInternet = pingGoogle()
            postValue(hasInternet)
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        // Contains list of connected networks. If empty, livedata emit false, otherwise true
        private val validNetworks = mutableSetOf<Network>()

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            val hasInternet = pingGoogle()
            if (hasInternet) {
                validNetworks.add(network)
            }
            postValue(validNetworks.isNotEmpty())
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            validNetworks.remove(network)
            postValue(validNetworks.isNotEmpty())
        }

    }

    /**
     * Ping Google to verify active network has internet
     */
    private fun pingGoogle(): Boolean {
        return try{
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        }catch (e: IOException){
            false
        }
    }

    /**
     * Check if network connections available
     * Return - true if has network connection, otherwise false
     */
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
             networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> true
            else -> false
        }
    }
}