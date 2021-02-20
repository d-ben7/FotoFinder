package com.example.fotofinder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.fotofinder.model.UnsplashResponse
import com.example.fotofinder.databinding.PhotoItemBinding

/**
 * Recycler view adapter for the gallery
 * @param itemClickListener - click listener for the photo item
 */
class PhotoAdapter(private val itemClickListener: OnItemClickListener) :
    PagingDataAdapter<UnsplashResponse.Photo, PhotoAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashResponse.Photo>() {
            override fun areItemsTheSame(oldItem: UnsplashResponse.Photo, newItem: UnsplashResponse.Photo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashResponse.Photo, newItem: UnsplashResponse.Photo): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position)!!
        holder.bind(photo, itemClickListener)
    }

    class ViewHolder(private val binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: UnsplashResponse.Photo, itemClickListener: OnItemClickListener) {
            // Setting aspect ratio to the ImageView according to the width & height of photo.
            // This solved the issue with laggy/jumpy item loading during recycling/scrolling items
            val ratio= String.format("%d:%d", photo.width, photo.height)
            ConstraintSet().apply {
                clone(binding.photoItemConstraintLayout)
                setDimensionRatio(binding.ivPhoto.id, ratio)
                applyTo(binding.photoItemConstraintLayout)
            }

            // Loading image using Coil
            binding.ivPhoto.load(photo.urls.small) {
                crossfade(75)
            }

            binding.ivPhoto.setOnClickListener {
                it.transitionName = "photoTransition"
                val extras = FragmentNavigatorExtras(it to "photoTransition")
                itemClickListener.onItemClicked(photo, extras)
            }

        }

    }

    interface OnItemClickListener{
        fun onItemClicked(photo: UnsplashResponse.Photo, extras: FragmentNavigator.Extras)
    }

}