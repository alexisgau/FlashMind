package com.alexisgau.synapai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alexisgau.synapai.data.local.dao.CategoryDao
import com.alexisgau.synapai.data.local.dao.FlashCardDao
import com.alexisgau.synapai.data.local.dao.LessonDao
import com.alexisgau.synapai.data.local.dao.QuizDao
import com.alexisgau.synapai.data.local.dao.SummaryDao
import com.alexisgau.synapai.data.local.entities.CategoryEntity
import com.alexisgau.synapai.data.local.entities.FlashCardEntity
import com.alexisgau.synapai.data.local.entities.LessonEntity
import com.alexisgau.synapai.data.local.entities.MultipleChoiceQuestionEntity
import com.alexisgau.synapai.data.local.entities.SummaryEntity
import com.alexisgau.synapai.data.local.entities.TestEntity


@Database(
    entities = [CategoryEntity::class, LessonEntity::class, FlashCardEntity::class, TestEntity::class, MultipleChoiceQuestionEntity::class, SummaryEntity::class],
    version = 4,
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