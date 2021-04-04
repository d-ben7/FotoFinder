package com.example.fotofinder.util

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class NetworkStatusHelperTest {

    @Before
    fun setup() {
        NetworkStatusHelper.init(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun wifiConnected_isNetworkAvailable_returnTrue() {
        val result = NetworkStatusHelper.isNetworkAvailable()
        assertThat(result).isTrue()
    }

    @Test
    fun cellularConnected_isNetworkAvailable_returnTrue() {
        val result = NetworkStatusHelper.isNetworkAvailable()
        assertThat(result).isTrue()
    }

    @Test
    fun airPlaneModeActive_isNetworkAvailable_returnFalse() {
        val result = NetworkStatusHelper.isNetworkAvailable()
        assertThat(result).isFalse()
    }

    @Test
    fun wifiCellularDisconnected_isNetworkAvailable_returnFalse() {
        val result = NetworkStatusHelper.isNetworkAvailable()
        assertThat(result).isFalse()
    }

}