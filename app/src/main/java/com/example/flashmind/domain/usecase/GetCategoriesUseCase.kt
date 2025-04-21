package com.example.flashmind.domain.usecase

import android.util.Log
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.reposotory.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {

    operator fun invoke() : Flow<List<Category>> {
       return categoryRepository.getCategories()
    }
}