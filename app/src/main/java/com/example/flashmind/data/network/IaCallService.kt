package com.example.flashmind.data.network

import com.example.flashmind.domain.model.GenerateContentRequest
import com.example.flashmind.domain.model.GenerateContentResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IaCallService {

    @POST("v1/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
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
