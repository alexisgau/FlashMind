package com.example.flashmind.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flashmind.data.local.dao.CategoryDao
import com.example.flashmind.data.local.dao.FlashCardDao
import com.example.flashmind.data.local.dao.LessonDao
import com.example.flashmind.data.local.entities.CategoryEntity
import com.example.flashmind.data.local.entities.FlashCardEntity
import com.example.flashmind.data.local.entities.LessonEntity

@Database(entities = [CategoryEntity::class, LessonEntity::class, FlashCardEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun lessonDao(): LessonDao
    abstract fun flashCardDao(): FlashCardDao


}