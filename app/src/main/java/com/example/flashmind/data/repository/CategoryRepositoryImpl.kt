package com.example.flashmind.data.repository

import com.example.flashmind.data.local.dao.CategoryDao
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.local.entities.toEntity
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.reposotory.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao):CategoryRepository {
    override suspend fun insertCategory(category: Category) {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        return categoryDao.deleteCategory(category.toEntity())
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.toDomain() }
        }
    }
}




