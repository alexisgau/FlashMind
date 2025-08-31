package com.example.flashmind.presentation.ui.addlesson

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.usecase.lesson.InsertLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLessonViewModel @Inject constructor(private val insertLessonUseCase: InsertLessonUseCase,): ViewModel() {



    fun insertLesson(lesson: Lesson) {
        viewModelScope.launch {
            runCatching { insertLessonUseCase(lesson) }
                .onFailure { e ->
                    Log.e("HomeViewModel", "insertLesson: $e")
                }
        }
    }
}