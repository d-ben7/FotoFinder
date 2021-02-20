package com.example.fotofinder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fotofinder.R
import com.example.fotofinder.databinding.ActivityMainBinding
import com.example.fotofinder.util.NetworkStatusHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        // Observe the status of network connections. Show dialog if there is no network connection
        NetworkStatusHelper.observe(this, { isConnected ->
            if (isConnected.not()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.networkErrorDialogTitle))
                    .setMessage(resources.getString(R.string.networkErrorDialogMessage))
                    .setPositiveButton("Close", null)
                    .show()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        NetworkStatusHelper.removeObservers(this)
    }
}