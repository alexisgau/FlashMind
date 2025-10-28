package com.example.flashmind.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData
import com.example.flashmind.domain.usecase.category.DeleteCategoryUseCase
import com.example.flashmind.domain.usecase.category.GetCategoriesUseCase
import com.example.flashmind.domain.usecase.category.GetLessonCountByCategory
import com.example.flashmind.domain.usecase.lesson.DeleteLessonUseCase
import com.example.flashmind.domain.usecase.lesson.GetLessonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getLessonCountByCategory: GetLessonCountByCategory,
    private val authClient: AuthClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    private val _categoryToDelete = MutableStateFlow<Category?>(null)
    val categoryToDelete: StateFlow<Category?> = _categoryToDelete.asStateFlow()

    private val _lessonToDelete = MutableStateFlow<Lesson?>(null)
    val lessonToDelete: StateFlow<Lesson?> = _lessonToDelete.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Carga categorías, conteos y datos de usuario en paralelo (o secuencial si hay dependencias)
            try {
                // Inicia en Loading
                _uiState.value = HomeUiState.Loading

                // Carga categorías y espera el primer valor
                val categories = getCategoriesUseCase().first() // Toma la primera lista emitida

                // Carga los conteos para esas categorías
                val counts = mutableMapOf<Int, Int>()
                categories.forEach { category ->
                    try {
                        counts[category.id] = getLessonCountByCategory(category.id).first()
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error loading lesson count for category ${category.id}", e)
                        counts[category.id] = 0 // Default a 0 si falla
                    }
                }

                // Carga datos de usuario
                val userData = getUserDataInternal() // Función auxiliar

                // Emite el estado Success inicial (sin lecciones cargadas, sin expansiones)
                _uiState.value = HomeUiState.Success(
                    categories = categories,
                    lessonsMap = emptyMap(),
                    expandedCategoryIds = emptySet(),
                    lessonCounts = counts,
                    userData = userData
                )

                // Opcional: Podrías empezar a observar cambios en categorías aquí si es necesario
                // observeCategoryChanges()


            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading initial data", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Error cargando datos iniciales")
            }
        }
    }

    // Función auxiliar síncrona/rápida para obtener datos de usuario
    private fun getUserDataInternal(): UserData {
        val user = runCatching { authClient.getCurrentUser() }.getOrNull()
        val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "User"
        return when {
            user != null -> UserData.Success(
                name = name,
                imageUrl = user.photoUrl?.toString().orEmpty()
            )
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

            // Si se expandió y no tenemos las lecciones, las cargamos
            if (newExpandedIds.contains(categoryId) && currentState.lessonsMap[categoryId] == null) {
                loadLessonsForCategory(categoryId)
            }
        }
    }

    private fun loadLessonsForCategory(categoryId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            // Solo carga si estamos en estado Success
            if (currentState is HomeUiState.Success) {
                // Opcional: Podrías poner un indicador de carga *dentro* del mapa de lecciones
                // val loadingMap = currentState.lessonsMap + (categoryId to null) // null indica cargando?
                // _uiState.value = currentState.copy(lessonsMap = loadingMap)
                try {
                    getLessonsUseCase(categoryId)
                        .catch { e ->
                            Log.e("HomeViewModel", "Error loading lessons for category $categoryId", e)
                            // Actualiza el mapa indicando error o lista vacía para esta categoría
                            val errorMap = currentState.lessonsMap + (categoryId to emptyList()) // Lista vacía en error
                            _uiState.value = currentState.copy(lessonsMap = errorMap)
                        }
                        .collectLatest { lessons ->
                            // Actualiza el mapa con las lecciones cargadas
                            val newMap = currentState.lessonsMap + (categoryId to lessons)
                            // Re-emitimos el estado Success con el mapa actualizado
                            // Es importante obtener el estado MÁS RECIENTE aquí por si cambió mientras cargaba
                            val latestState = _uiState.value
                            if(latestState is HomeUiState.Success) {
                                _uiState.value = latestState.copy(lessonsMap = newMap)
                            }
                        }
                } catch (e: Exception) { // Captura por si el Flow mismo lanza excepción inicial
                    Log.e("HomeViewModel", "Error collecting lessons flow for category $categoryId", e)
                    val errorMap = currentState.lessonsMap + (categoryId to emptyList())
                    if(_uiState.value is HomeUiState.Success) { // Check again before updating
                        _uiState.value = ( _uiState.value as HomeUiState.Success).copy(lessonsMap = errorMap)
                    }
                }
            }
        }
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
                // Actualización optimista: Quita la categoría de la lista
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(
                        categories = currentState.categories.filterNot { it.id == category.id },
                        // También quita sus lecciones y conteo del estado actual
                        lessonsMap = currentState.lessonsMap - category.id,
                        lessonCounts = currentState.lessonCounts - category.id,
                        expandedCategoryIds = currentState.expandedCategoryIds - category.id
                    )
                }
                _categoryToDelete.value = null // Cierra diálogo
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting category ${category.id}", e)
                // Podrías emitir un evento de error a la UI (Snackbar)
                _categoryToDelete.value = null // Cierra diálogo igualmente
            }
        }
    }

    fun confirmDeleteLesson() {
        val lesson = _lessonToDelete.value ?: return
        viewModelScope.launch {
            try {
                deleteLessonUseCase(lesson)
                Log.i("HomeViewModel", "Lesson ${lesson.id} marked for deletion.")
                // Actualización optimista: Quita la lección del mapa
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    val currentLessonsForCat = currentState.lessonsMap[lesson.categoryId]
                    if (currentLessonsForCat != null) {
                        val updatedLessons = currentLessonsForCat.filterNot { it.id == lesson.id }
                        val newMap = currentState.lessonsMap + (lesson.categoryId to updatedLessons)
                        // Actualiza también el contador
                        val newCounts = currentState.lessonCounts + (lesson.categoryId to (currentState.lessonCounts[lesson.categoryId] ?: 1) - 1)

                        _uiState.value = currentState.copy(lessonsMap = newMap, lessonCounts = newCounts)
                    }
                }
                _lessonToDelete.value = null // Cierra diálogo
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting lesson ${lesson.id}", e)
                // Podrías emitir un evento de error a la UI (Snackbar)
                _lessonToDelete.value = null // Cierra diálogo igualmente
            }
        }
    }
}