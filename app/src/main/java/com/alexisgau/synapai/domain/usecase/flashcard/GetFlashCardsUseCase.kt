package com.alexisgau.synapai.domain.usecase.flashcard

import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.reposotory.FlashCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlashCardsUseCase @Inject constructor(private val flashCardRepository: FlashCardRepository) {

    operator fun invoke(lessonId: Int): Flow<List<FlashCard>> {

        return flashCardRepository.getFlashCardsByLesson(lessonId)

    }
}