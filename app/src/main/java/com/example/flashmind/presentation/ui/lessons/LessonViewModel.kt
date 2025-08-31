package com.example.flashmind.presentation.ui.lessons

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.usecase.lesson.DeleteLessonUseCase
import com.example.flashmind.domain.usecase.lesson.GetLessonsUseCase
import com.example.flashmind.presentation.ui.home.LessonsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase,
) : ViewModel() {

    private val _lessonsState = MutableStateFlow<LessonsState>(LessonsState.Loading)
    val lessonsState: StateFlow<LessonsState> = _lessonsState.asStateFlow()

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

    fun deleteLesson(lesson: Lesson) {

        viewModelScope.launch {
            runCatching { deleteLessonUseCase.invoke(lesson) }.onFailure { exception ->
                Log.e("HomeViewModel", "delete category: $exception")
            }

        }

    }
}