package com.alexisgau.synapai.domain.usecase.lesson

import com.alexisgau.synapai.domain.model.Lesson
import com.alexisgau.synapai.domain.reposotory.LessonRepository
import javax.inject.Inject

class DeleteLessonUseCase @Inject constructor(private val lessonRepository: LessonRepository) {

    suspend operator fun invoke(lesson: Lesson) {
        return lessonRepository.deleteLesson(lesson)
    }
}