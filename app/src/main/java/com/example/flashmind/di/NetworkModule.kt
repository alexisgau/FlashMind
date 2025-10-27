package com.example.flashmind.di

import android.content.Context
import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.data.network.AuthInterceptor
import com.example.flashmind.data.network.GeminiDataSource
import com.example.flashmind.data.network.IaCallService
import com.example.flashmind.data.repository.AiRepositoryImpl
import com.example.flashmind.data.repository.AuthRepositoryImpl
import com.example.flashmind.domain.reposotory.AiRepository
import com.example.flashmind.domain.reposotory.AuthRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(authInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(@ApplicationContext context: Context): AuthInterceptor {
        return AuthInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApiService(retrofit: Retrofit): IaCallService {
        return retrofit.create(IaCallService::class.java)
    }

    @Provides
    @Singleton
    fun provideAiRepository(
        geminiDataSource: GeminiDataSource
    ): AiRepository {
        return AiRepositoryImpl(geminiDataSource)
    }


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authClient: AuthClient): AuthRepository {
        return AuthRepositoryImpl(authClient)
    }


    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
