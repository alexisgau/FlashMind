package com.example.flashmind.presentation.ui.home

import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson

sealed interface CategoryState {
    object Loading : CategoryState
    data class Success(val categories: List<Category>) : CategoryState
    data class Error(val message: String) : CategoryState
}

sealed interface AddCategoryState {
    object Loading : AddCategoryState
    object Success : AddCategoryState
    data class Error(val message: String) : AddCategoryState
}

sealed interface LessonsState{

    object  Loading: LessonsState
    object IsEmpty: LessonsState
    data class Success(val lessons: List<Lesson>): LessonsState
    data class Error(val message: String) : LessonsState

}