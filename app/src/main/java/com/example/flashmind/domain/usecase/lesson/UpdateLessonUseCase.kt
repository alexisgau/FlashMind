package com.example.flashmind.domain.usecase.lesson

import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.reposotory.LessonRepository
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val repository: LessonRepository,
) {
    suspend operator fun invoke(lesson: Lesson) {
        repository.updateLesson(lesson)
    }
}