package com.example.flashmind.di

import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.data.network.GeminiDataSource
import com.example.flashmind.data.repository.AiRepositoryImpl
import com.example.flashmind.data.repository.AuthRepositoryImpl
import com.example.flashmind.domain.reposotory.AiRepository
import com.example.flashmind.domain.reposotory.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGeminiDataSource(): GeminiDataSource {
        return GeminiDataSource()
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
