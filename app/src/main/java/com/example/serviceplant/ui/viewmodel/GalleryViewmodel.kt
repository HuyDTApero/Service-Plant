package com.example.serviceplant.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceplant.model.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewmodel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _listImage : MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
            val listImage : StateFlow<List<Uri>> = _listImage
    private var currentImage : Uri? = null

    init {
        viewModelScope.launch {
            _listImage.value = imageRepository.getAllImages()
        }
    }
    fun setCurrentImage(uri : Uri){
        currentImage = uri
    }
    fun getCurrentImage() = currentImage
}