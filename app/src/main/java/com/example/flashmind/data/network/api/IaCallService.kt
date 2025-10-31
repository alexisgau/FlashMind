package com.example.flashmind.data.network.api

import com.example.flashmind.data.network.dto.GenerateContentRequest
import com.example.flashmind.data.network.dto.GenerateContentResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IaCallService {

    @POST("v1/models/{modelName}:generateContent")
    suspend fun generateContent(
        @Path("modelName") modelName: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse

    @GET("v1/models")
    suspend fun listModels(@Query("key") apiKey: String): ModelsResponse
}

@Serializable
data class ModelsResponse(
    val models: List<ModelInfo>
)

@Serializable
data class ModelInfo(
    val name: String,
    val version: String,
    val displayName: String,
    val description: String,
    val supportedGenerationMethods: List<String>
)
