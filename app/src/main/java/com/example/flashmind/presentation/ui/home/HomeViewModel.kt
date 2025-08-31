package com.example.flashmind.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.UserData
import com.example.flashmind.domain.usecase.category.DeleteCategoryUseCase
import com.example.flashmind.domain.usecase.category.GetCategoriesUseCase
import com.example.flashmind.domain.usecase.category.GetLessonCountByCategory
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
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getLessonCountByCategory: GetLessonCountByCategory,
    private val authClient: AuthClient
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categoriesState: StateFlow<CategoryState> = _categoriesState.asStateFlow()

    private val _lessonCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val lessonCounts: StateFlow<Map<Int, Int>> = _lessonCounts


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
        val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "User"

        _userData.value = when {
            user != null -> UserData.Success(
                name = name,
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
                    categories.forEach { it.copy() }
                    _categoriesState.value = CategoryState.Success(categories)
                }
        }
    }


    fun deleteCategory(category: Category) {

        viewModelScope.launch {
            runCatching { deleteCategoryUseCase.invoke(category) }.onFailure { exception ->
                Log.e("HomeViewModel", "delete category: $exception")
            }

        }

    }

    fun observeLessonCount(categoryId: Int) {
        viewModelScope.launch {
            getLessonCountByCategory(categoryId).collectLatest { count ->
                _lessonCounts.value = _lessonCounts.value.toMutableMap().apply {
                    this[categoryId] = count
                }
            }
        }
    }


}