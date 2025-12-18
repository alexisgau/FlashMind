package com.alexisgau.synapai.presentation.ui.home

import com.alexisgau.synapai.domain.model.Category
import com.alexisgau.synapai.domain.model.Lesson
import com.alexisgau.synapai.domain.model.UserData

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val categories: List<Category>,
        val lessonsMap: Map<Int, List<Lesson>>,
        val expandedCategoryIds: Set<Int>,
        val lessonCounts: Map<Int, Int>,
        val userData: UserData,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}


sealed interface AddCategoryState {
    object Loading : AddCategoryState
    object Success : AddCategoryState
    data class Error(val message: String) : AddCategoryState
}

sealed interface LessonsState {

    object Loading : LessonsState
    data class Success(val lessons: List<Lesson>) : LessonsState
    data class Error(val message: String) : LessonsState

}