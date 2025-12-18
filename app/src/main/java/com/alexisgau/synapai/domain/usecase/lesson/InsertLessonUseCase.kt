package com.alexisgau.synapai.domain.usecase.lesson

import com.alexisgau.synapai.domain.model.Lesson
import com.alexisgau.synapai.domain.reposotory.LessonRepository
import javax.inject.Inject

class InsertLessonUseCase @Inject constructor(private val lessonRepository: LessonRepository) {

    suspend operator fun invoke(lesson: Lesson) = lessonRepository.insert(lesson)

}