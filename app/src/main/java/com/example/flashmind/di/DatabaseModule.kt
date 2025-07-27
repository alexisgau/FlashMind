package com.example.flashmind.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.WorkerFactory
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
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
    @Singleton
    fun provideCategoryRepository(
        auth: FirebaseAuth,
        categoryDao: CategoryDao,
        firestore: FirebaseFirestore
    ): CategoryRepository {
        return CategoryRepositoryImpl(auth,categoryDao, firestore)
    }


    @Provides
    fun provideLessonDao(database: AppDatabase): LessonDao {
        return database.lessonDao()
    }

    @Provides
    fun provideLessonRepository(
        dao: LessonDao,
        auth: FirebaseAuth
    ): LessonRepository {
        return LessonRepositoryImpl(dao,auth)
    }

    @Provides
    fun provideFlashDao(database: AppDatabase): FlashCardDao {
        return database.flashCardDao()
    }

    @Provides
    fun provideFlashCardRepository(
        auth: FirebaseAuth,
        dao: FlashCardDao
    ): FlashCardRepository {
        return FlashCardRepositoryImpl(auth = auth, dao = dao)
    }


}