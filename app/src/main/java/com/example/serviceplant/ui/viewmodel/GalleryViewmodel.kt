package com.example.serviceplant.ui.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviceplant.model.data.remote.PlantResponse
import com.example.serviceplant.model.repository.ImageRepository
import com.example.serviceplant.model.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.awaitResponse
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GalleryViewmodel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val plantRepository: PlantRepository
) : ViewModel() {
    private val _listImage : MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
            val listImage : StateFlow<List<Uri>> = _listImage
    private var currentImage : Uri? = null

    private val _plantResponse: MutableLiveData<PlantResponse?> = MutableLiveData()
    val plantResponse: LiveData<PlantResponse?> = _plantResponse

    init {
        viewModelScope.launch {
            _listImage.value = imageRepository.getAllImages()
        }
    }
    fun setCurrentImage(uri : Uri){
        currentImage = uri
    }
    fun getCurrentImage() = currentImage

    @RequiresApi(Build.VERSION_CODES.O)
    fun identifyPlant(bundleId: String, apiToken: String, appName: String) {
        val currentUri = currentImage ?: return

        viewModelScope.launch {
            try {
                val file = imageUriToMultipart(currentUri)

                val response = plantRepository.identifyPlant2(
                    file = file,
                    bundleId = bundleId,
                    apiToken = apiToken,
                    appName = appName
                ).execute()

                if (response.isSuccessful) {
                    _plantResponse.postValue(response.body())
                } else {
                    _plantResponse.postValue(null) // Handle error case
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _plantResponse.postValue(null)
            }
        }
    }

    private fun imageUriToMultipart(uri: Uri): MultipartBody.Part {
        val file = File(uri.path)
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }
}