package com.example.serviceplant.model.repository

import android.net.Uri
import javax.inject.Inject

class ImageRepository @Inject constructor(private val contentResolverHelper: ContentResolverHelper) {
    suspend fun getAllImages(): List<Uri> = contentResolverHelper.getAllImages()
}