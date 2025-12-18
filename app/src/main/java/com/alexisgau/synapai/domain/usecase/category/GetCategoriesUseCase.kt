package com.alexisgau.synapai.domain.usecase.category

import com.alexisgau.synapai.domain.model.Category
import com.alexisgau.synapai.domain.reposotory.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {

    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getCategories()
    }
}