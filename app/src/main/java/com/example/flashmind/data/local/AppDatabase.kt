package com.example.flashmind.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flashmind.data.local.dao.CategoryDao
import com.example.flashmind.data.local.dao.FlashCardDao
import com.example.flashmind.data.local.dao.LessonDao
import com.example.flashmind.data.local.dao.QuizDao
import com.example.flashmind.data.local.dao.SummaryDao
import com.example.flashmind.data.local.entities.CategoryEntity
import com.example.flashmind.data.local.entities.FlashCardEntity
import com.example.flashmind.data.local.entities.LessonEntity
import com.example.flashmind.data.local.entities.MultipleChoiceQuestionEntity
import com.example.flashmind.data.local.entities.SummaryEntity
import com.example.flashmind.data.local.entities.TestEntity


@Database(
    entities = [CategoryEntity::class, LessonEntity::class, FlashCardEntity::class, TestEntity::class, MultipleChoiceQuestionEntity::class, SummaryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun lessonDao(): LessonDao
    abstract fun flashCardDao(): FlashCardDao
    abstract fun quizDao(): QuizDao

    abstract fun summaryDao(): SummaryDao


}