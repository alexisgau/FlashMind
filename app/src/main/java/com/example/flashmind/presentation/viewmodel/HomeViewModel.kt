package com.example.flashmind.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData
import com.example.flashmind.domain.usecase.GetCategoriesUseCase
import com.example.flashmind.domain.usecase.GetLessonsUseCase
import com.example.flashmind.domain.usecase.InsertCategoryUseCase
import com.example.flashmind.domain.usecase.InsertLessonUseCase
import com.example.flashmind.presentation.ui.home.AddCategoryState
import com.example.flashmind.presentation.ui.home.CategoryState
import com.example.flashmind.presentation.ui.home.LessonsState
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
    private val insertLessonUseCase: InsertLessonUseCase,
    private val authClient: AuthClient
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categoriesState: StateFlow<CategoryState> = _categoriesState.asStateFlow()

    private val _addCategoryState = MutableStateFlow<AddCategoryState>(AddCategoryState.Loading)
    val addCategoryState: StateFlow<AddCategoryState> = _addCategoryState.asStateFlow()

    private val _lessonsState = MutableStateFlow<LessonsState>(LessonsState.Loading)
    val lessonsState: StateFlow<LessonsState> = _lessonsState.asStateFlow()

    private val _userData = MutableStateFlow<UserData>(UserData.Init)
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        getAllCategories()
        getUserData()
    }

    private fun getUserData() {
        val user = runCatching { authClient.getCurrentUser() }
            .getOrNull()

        _userData.value = when {
            user != null -> UserData.Success(
                name = user.displayName ?: "Usuario",
                imageUrl = user.photoUrl?.toString().orEmpty()
            )
            else -> UserData.Error("Usuario no autenticado")
        }
    }

    private fun getAllCategories() {
        _categoriesState.value = CategoryState.Loading
        viewModelScope.launch {
            getCategoriesUseCase()
                .catch { e ->
                    _categoriesState.value = CategoryState.Error(e.message ?: "Error desconocido")
                }
                .collect { categories ->
                    _categoriesState.value = CategoryState.Success(categories)
                }
        }
    }

    fun insertCategory(category: Category) {
        _addCategoryState.value = AddCategoryState.Loading
        viewModelScope.launch {
            runCatching { insertCategoryUseCase(category) }
                .onSuccess { _addCategoryState.value = AddCategoryState.Success }
                .onFailure { e ->
                    Log.e("HomeViewModel", "insertCategory: $e")
                    _addCategoryState.value = AddCategoryState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun getAllLessonsByCategory(categoryId: Int) {
        _lessonsState.value = LessonsState.Loading
        viewModelScope.launch {
            runCatching {
                getLessonsUseCase(categoryId)
                    .collectLatest { lessons ->
                        _lessonsState.value = if (lessons.isEmpty())
                            LessonsState.IsEmpty
                        else
                            LessonsState.Success(lessons)
                    }
            }.onFailure { e ->
                Log.e("HomeViewModel", "getAllLessonsByCategory: $e")
                _lessonsState.value = LessonsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun insertLesson(lesson: Lesson) {
        viewModelScope.launch {
            runCatching { insertLessonUseCase(lesson) }
                .onFailure { e ->
                    Log.e("HomeViewModel", "insertLesson: $e")
                }
        }
    }
}


