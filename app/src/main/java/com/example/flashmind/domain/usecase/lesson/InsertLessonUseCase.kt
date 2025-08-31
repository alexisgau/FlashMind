package com.example.flashmind.domain.usecase.lesson

import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.reposotory.LessonRepository
import javax.inject.Inject

class InsertLessonUseCase @Inject constructor(private val lessonRepository: LessonRepository) {

    suspend operator fun invoke(lesson: Lesson) = lessonRepository.insert(lesson)

}