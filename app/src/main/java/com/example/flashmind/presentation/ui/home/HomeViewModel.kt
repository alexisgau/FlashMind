package com.example.flashmind.presentation.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.R
import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData
import com.example.flashmind.domain.usecase.auth.UpdateUserNameUseCase
import com.example.flashmind.domain.usecase.category.DeleteCategoryUseCase
import com.example.flashmind.domain.usecase.category.GetCategoriesUseCase
import com.example.flashmind.domain.usecase.category.GetLessonCountByCategory
import com.example.flashmind.domain.usecase.lesson.DeleteLessonUseCase
import com.example.flashmind.domain.usecase.lesson.GetLessonsUseCase
import com.example.flashmind.domain.usecase.lesson.UpdateLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getLessonsUseCase: GetLessonsUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val getLessonCountByCategory: GetLessonCountByCategory,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val authClient: AuthClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    private val _categoryToDelete = MutableStateFlow<Category?>(null)
    val categoryToDelete: StateFlow<Category?> = _categoryToDelete.asStateFlow()

    private val _lessonToDelete = MutableStateFlow<Lesson?>(null)
    val lessonToDelete: StateFlow<Lesson?> = _lessonToDelete.asStateFlow()

    private val _lessonToEdit = MutableStateFlow<Lesson?>(null)
    val lessonToEdit: StateFlow<Lesson?> = _lessonToEdit.asStateFlow()

    private val _showNameInput = MutableStateFlow(false)
    val showNameInput: StateFlow<Boolean> = _showNameInput.asStateFlow()

    init {
        loadInitialData()
        checkUserName()
    }


    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading
                val categories = getCategoriesUseCase().first()

                // Carga los conteos para esas categorías
                val counts = mutableMapOf<Int, Int>()
                categories.forEach { category ->
                    try {
                        counts[category.id] = getLessonCountByCategory(category.id).first()
                    } catch (e: Exception) {
                        Log.e(
                            "HomeViewModel",
                            "Error loading lesson count for category ${category.id}",
                            e
                        )
                        counts[category.id] = 0
                    }
                }

                // Carga datos de usuario
                val userData = getUserDataInternal()

                _uiState.value = HomeUiState.Success(
                    categories = categories,
                    lessonsMap = emptyMap(),
                    expandedCategoryIds = emptySet(),
                    lessonCounts = counts,
                    userData = userData
                )

                checkUserName()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading initial data", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Error cargando datos iniciales")
            }
        }
    }

    private fun checkUserName() {
        val user = authClient.getCurrentUser()
        if (user != null && !user.isAnonymous && user.displayName.isNullOrBlank()) {
            _showNameInput.value = true
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            updateUserNameUseCase(name)
                .onSuccess {
                    _showNameInput.value = false
                    val newState = _uiState.value
                    if (newState is HomeUiState.Success) {
                        _uiState.value = newState.copy(userData = getUserDataInternal())
                    }
                }
                .onFailure {
                    Log.e("HomeViewModel", "Error updating name", it)

                }
        }
    }

    private fun getUserDataInternal(): UserData {
        val user = runCatching { authClient.getCurrentUser() }.getOrNull()

        return when {
            user != null && user.isAnonymous -> {

                UserData.Success(
                    name = context.getString(R.string.home_guest_name),
                    imageUrl = "",
                    isAnonymous = true
                )
            }
            user != null -> {
                val displayName = user.displayName

                val nameToShow = if (!displayName.isNullOrBlank()) {
                    displayName
                } else {
                    ""
                }

                UserData.Success(
                    name = nameToShow,
                    imageUrl = user.photoUrl?.toString().orEmpty(),
                    isAnonymous = false
                )
            }
            else -> UserData.Error("Usuario no autenticado")
        }
    }


    fun toggleCategoryExpansion(categoryId: Int) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val currentExpandedIds = currentState.expandedCategoryIds
            val newExpandedIds = if (currentExpandedIds.contains(categoryId)) {
                currentExpandedIds - categoryId
            } else {
                currentExpandedIds + categoryId
            }

            _uiState.value = currentState.copy(expandedCategoryIds = newExpandedIds)

            if (newExpandedIds.contains(categoryId) && currentState.lessonsMap[categoryId] == null) {
                loadLessonsForCategory(categoryId)
            }
        }
    }

    private fun loadLessonsForCategory(categoryId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState is HomeUiState.Success) {
                try {
                    getLessonsUseCase(categoryId)
                        .catch { e ->
                            Log.e(
                                "HomeViewModel",
                                "Error loading lessons for category $categoryId",
                                e
                            )
                            val errorMap =
                                currentState.lessonsMap + (categoryId to emptyList()) // Lista vacía en error
                            _uiState.value = currentState.copy(lessonsMap = errorMap)
                        }
                        .collectLatest { lessons ->
                            val newMap = currentState.lessonsMap + (categoryId to lessons)
                            val latestState = _uiState.value
                            if (latestState is HomeUiState.Success) {
                                _uiState.value = latestState.copy(lessonsMap = newMap)
                            }
                        }
                } catch (e: Exception) {
                    Log.e(
                        "HomeViewModel",
                        "Error collecting lessons flow for category $categoryId",
                        e
                    )
                    val errorMap = currentState.lessonsMap + (categoryId to emptyList())
                    if (_uiState.value is HomeUiState.Success) {
                        _uiState.value =
                            (_uiState.value as HomeUiState.Success).copy(lessonsMap = errorMap)
                    }
                }
            }
        }
    }

    fun requestLessonEdit(lesson: Lesson) {
        _lessonToEdit.value = lesson
    }

    fun cancelEditOrDelete() {
        _categoryToDelete.value = null
        _lessonToDelete.value = null
        _lessonToEdit.value = null
    }
    fun requestCategoryDeletion(category: Category) {
        _categoryToDelete.value = category
    }

    fun requestLessonDeletion(lesson: Lesson) {
        _lessonToDelete.value = lesson
    }

    fun cancelDeletion() {
        _categoryToDelete.value = null
        _lessonToDelete.value = null
    }

    fun confirmDeleteCategory() {
        val category = _categoryToDelete.value ?: return
        viewModelScope.launch {
            try {
                deleteCategoryUseCase(category)
                Log.i("HomeViewModel", "Category ${category.id} marked for deletion.")
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(
                        categories = currentState.categories.filterNot { it.id == category.id },
                        lessonsMap = currentState.lessonsMap - category.id,
                        lessonCounts = currentState.lessonCounts - category.id,
                        expandedCategoryIds = currentState.expandedCategoryIds - category.id
                    )
                }
                _categoryToDelete.value = null
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting category ${category.id}", e)
                _categoryToDelete.value = null
            }
        }
    }

    fun confirmDeleteLesson() {
        val lesson = _lessonToDelete.value ?: return
        viewModelScope.launch {
            try {
                deleteLessonUseCase(lesson)
                Log.i("HomeViewModel", "Lesson ${lesson.id} marked for deletion.")
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    val currentLessonsForCat = currentState.lessonsMap[lesson.categoryId]
                    if (currentLessonsForCat != null) {
                        val updatedLessons = currentLessonsForCat.filterNot { it.id == lesson.id }
                        val newMap = currentState.lessonsMap + (lesson.categoryId to updatedLessons)
                        val newCounts =
                            currentState.lessonCounts + (lesson.categoryId to (currentState.lessonCounts[lesson.categoryId]
                                ?: 1) - 1)

                        _uiState.value =
                            currentState.copy(lessonsMap = newMap, lessonCounts = newCounts)
                    }
                }
                _lessonToDelete.value = null
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting lesson ${lesson.id}", e)
                _lessonToDelete.value = null
            }
        }
    }


    fun confirmLessonEdit(newName: String) {
        val lesson = _lessonToEdit.value ?: return
        val updatedLesson = lesson.copy(tittle = newName)

        viewModelScope.launch {
            try {
                updateLessonUseCase(updatedLesson)
                Log.i("HomeViewModel", "Lesson ${lesson.id} updated.")

                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    val currentLessons = currentState.lessonsMap[lesson.categoryId]
                    if (currentLessons != null) {
                        val updatedList = currentLessons.map {
                            if (it.id == updatedLesson.id) updatedLesson else it
                        }
                        val newMap = currentState.lessonsMap + (lesson.categoryId to updatedList)
                        _uiState.value = currentState.copy(lessonsMap = newMap)
                    }
                }
                _lessonToEdit.value = null
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error updating lesson ${lesson.id}", e)
                _lessonToEdit.value = null
            }
        }
    }
}