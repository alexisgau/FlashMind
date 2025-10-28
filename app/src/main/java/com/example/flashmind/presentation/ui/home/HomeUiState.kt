package com.example.flashmind.presentation.ui.home

import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val categories: List<Category>,
        val lessonsMap: Map<Int, List<Lesson>>,
        val expandedCategoryIds: Set<Int>,
        val lessonCounts: Map<Int, Int>,
        val userData: UserData
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

//sealed interface UserData {
//    data class Success(val name: String, val imageUrl: String) : UserData
//    data class Error(val message: String) : UserData
//    object Init : UserData
//}

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