package com.example.flashmind.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.usecase.GetCategoriesUseCase
import com.example.flashmind.domain.usecase.GetLessonsUseCase
import com.example.flashmind.domain.usecase.InsertCategoryUseCase
import com.example.flashmind.domain.usecase.InsertLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val insertCategoryUseCase: InsertCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getLessonsUseCase: GetLessonsUseCase,
    private val insertLessonUseCase: InsertLessonUseCase
) :
    ViewModel() {

    private val _categoriesState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categoriesState: StateFlow<CategoryState> = _categoriesState.asStateFlow()

    private val _uiState = MutableStateFlow<AddCategoryState>(AddCategoryState.Loading)
    val uiState: StateFlow<AddCategoryState> = _uiState.asStateFlow()

    private val _lessons = MutableStateFlow<LessonsState>(LessonsState.Loading)
    val lessons: StateFlow<LessonsState> = _lessons.asStateFlow()

    init {
        getAllCategories()
    }


    private fun getAllCategories() {
        _categoriesState.value = CategoryState.Loading
        viewModelScope.launch {
            getCategoriesUseCase.invoke().catch { e ->
                _categoriesState.value = CategoryState.Error(
                    message = e.message ?: "Error desconocido"
                )
            }
                .collect { categories ->
                    _categoriesState.value = CategoryState.Success(categories)
                }
        }
    }

    fun insertCategory(category: Category) {

        _uiState.value = AddCategoryState.Loading
        viewModelScope.launch {

            try {
                insertCategoryUseCase.invoke(category)
                _uiState.value = AddCategoryState.Success

            } catch (e: Exception) {
                Log.e("HomeViewModel", "error:$e")
                _uiState.value = AddCategoryState.Error(e.message ?: "Error desconocido")
            }


        }
    }

     fun getAllLessonByCategory(categoryId: Int){

        viewModelScope.launch {

            try {
                getLessonsUseCase.invoke(categoryId).collectLatest { lessons->

                    if(lessons.isNotEmpty()) {
                        _lessons.value = LessonsState.Success(lessons)
                    }else{
                        _lessons.value = LessonsState.IsEmpty
                    }
                }
            }catch (e: Exception){
                _lessons.value = LessonsState.Error(e.message ?: "error")
                Log.e("HomeViewModel", "getAllLessonByCategory: $e")
            }

        }

    }

    fun insertLesson(lesson: Lesson){

        viewModelScope.launch {

            try {
                insertLessonUseCase.invoke(lesson)
            }catch (e: Exception){

                Log.e("HomeViewModel", "ERROR: $e")
            }
        }

    }


}

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