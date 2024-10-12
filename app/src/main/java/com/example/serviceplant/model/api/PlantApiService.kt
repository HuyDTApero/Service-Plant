package com.example.serviceplant.model.api

import com.example.serviceplant.model.data.remote.PlantResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PlantIdentificationApi {
    @Multipart
    @POST("/api/v1/plant-identifier")
    fun identifyPlant(
        @Part file: MultipartBody.Part,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("X-Api-Signature") apiSignature: String,
        @Header("X-Api-BundleId") bundleId: String,
        @Header("X-Api-Timestamp") timestamp: String,
        @Header("X-Api-Token") apiToken: String,
        @Header("App-Name") appName: String
    ): Call<PlantResponse>
}