package com.example.flashmind.di

import android.content.Context
import androidx.room.Room
import com.example.flashmind.data.local.AppDatabase
import com.example.flashmind.data.local.dao.CategoryDao
import com.example.flashmind.data.local.dao.FlashCardDao
import com.example.flashmind.data.local.dao.LessonDao
import com.example.flashmind.data.repository.CategoryRepositoryImpl
import com.example.flashmind.data.repository.FlashCardRepositoryImpl
import com.example.flashmind.data.repository.LessonRepositoryImpl
import com.example.flashmind.domain.reposotory.CategoryRepository
import com.example.flashmind.domain.reposotory.FlashCardRepository
import com.example.flashmind.domain.reposotory.LessonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "flash_mind"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideCategoryRepository(
        categoryDao: CategoryDao
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }


    @Provides
    fun provideLessonDao(database: AppDatabase): LessonDao {
        return database.lessonDao()
    }

    @Provides
    fun provideLessonRepository(
        dao: LessonDao
    ): LessonRepository {
        return LessonRepositoryImpl(dao)
    }

    @Provides
    fun provideFlashDao(database: AppDatabase): FlashCardDao {
        return database.flashCardDao()
    }

    @Provides
    fun provideFlashCardRepository(
        dao: FlashCardDao
    ): FlashCardRepository {
        return FlashCardRepositoryImpl(dao)
    }
}