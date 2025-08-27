package com.example.flashmind.domain.usecase

import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.reposotory.LessonRepository
import javax.inject.Inject

class DeleteLessonUseCase @Inject constructor(private val lessonRepository: LessonRepository) {

    suspend operator fun invoke(lesson: Lesson){
        return lessonRepository.deleteLesson(lesson)
    }
}