package com.example.flashmind.domain.usecase.category

import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.reposotory.CategoryRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository){

    suspend operator fun invoke(category: Category){

        categoryRepository.insertCategory(category)
    }
}