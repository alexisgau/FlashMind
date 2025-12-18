package com.alexisgau.synapai.domain.usecase.category

import com.alexisgau.synapai.domain.model.Category
import com.alexisgau.synapai.domain.reposotory.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {

    suspend operator fun invoke(category: Category) {
        categoryRepository.deleteCategory(category)
    }

}

class GetLessonCountByCategory @Inject constructor(private val categoryRepository: CategoryRepository) {

    operator fun invoke(categoryId: Int): Flow<Int> {
        return categoryRepository.getLessonCountByCategory(categoryId)
    }

}
