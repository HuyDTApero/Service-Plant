package com.example.serviceplant.model.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContentResolverHelper @Inject constructor(@ApplicationContext val context: Context) {
    suspend fun getAllImages(): List<Uri> {
        return withContext(Dispatchers.IO) {
            val imageUris = mutableListOf<Uri>()
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (it.moveToNext()) {
                    val imageId = it.getLong(idColumn)
                    val imageUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageId.toString()
                    )
                    imageUris.add(imageUri)
                }
            }

            imageUris
        }
    }
}