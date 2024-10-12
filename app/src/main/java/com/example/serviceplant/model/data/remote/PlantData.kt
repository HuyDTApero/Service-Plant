package com.example.serviceplant.model.data.remote

data class PlantData(
    val scientificName: String,
    val commonName: String,
    val genus: String,
    val family: String,
    val avatar: String,
    val images: List<String>,
    val description: String,
    val confidence: Double
)

data class PlantResponse(
    val statusCode: Int,
    val message: String,
    val data: List<PlantData>,
    val timestamp: Long
)