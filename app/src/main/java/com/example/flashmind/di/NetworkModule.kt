package com.example.flashmind.di

import com.example.flashmind.data.network.GeminiDataSource
import com.example.flashmind.data.repository.AiRepositoryImpl
import com.example.flashmind.domain.reposotory.AiRepository
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
}
