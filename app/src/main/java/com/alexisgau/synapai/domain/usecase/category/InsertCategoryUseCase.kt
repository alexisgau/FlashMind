package com.alexisgau.synapai.domain.usecase.category

import com.alexisgau.synapai.domain.model.Category
import com.alexisgau.synapai.domain.reposotory.CategoryRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {

    suspend operator fun invoke(category: Category) {

        categoryRepository.insertCategory(category)
    }
}