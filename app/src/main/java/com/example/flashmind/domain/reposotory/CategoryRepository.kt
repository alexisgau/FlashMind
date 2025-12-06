package com.example.flashmind.domain.reposotory

import com.example.flashmind.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getCategories(): Flow<List<Category>>

    suspend fun getUnsyncedCategories(): List<Category>
    suspend fun uploadCategoryToFirestore(category: Category)
    suspend fun markCategoryAsSynced(categoryId: Int)

    suspend fun deleteCategoryFromFirestore(categoryId: Int)

    suspend fun deleteCategoryLocally(categoryId: Int)

    fun getLessonCountByCategory(categoryId: Int): Flow<Int>


}