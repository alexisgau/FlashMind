package com.example.flashmind.domain.usecase.category

import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.reposotory.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {

    suspend operator fun invoke(category: Category){
        categoryRepository.deleteCategory(category)
    }

}

class GetLessonCountByCategory @Inject constructor(private val categoryRepository: CategoryRepository) {

     operator fun invoke(categoryId: Int): Flow<Int> {
       return categoryRepository.getLessonCountByCategory(categoryId)
    }

}
