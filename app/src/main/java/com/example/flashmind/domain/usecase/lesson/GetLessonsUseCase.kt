package com.example.flashmind.domain.usecase.lesson

import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.reposotory.LessonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLessonsUseCase @Inject constructor(private val lessonRepository: LessonRepository) {

    operator fun invoke(categoryId: Int): Flow<List<Lesson>> =
        lessonRepository.getLessonsByCategory(categoryId)
}