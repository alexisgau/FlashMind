package com.alexisgau.synapai.domain.usecase.lesson

import com.alexisgau.synapai.domain.model.Lesson
import com.alexisgau.synapai.domain.reposotory.LessonRepository
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val repository: LessonRepository,
) {
    suspend operator fun invoke(lesson: Lesson) {
        repository.updateLesson(lesson)
    }
}