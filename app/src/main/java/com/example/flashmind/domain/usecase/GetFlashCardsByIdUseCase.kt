package com.example.flashmind.domain.usecase

import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.FlashCardRepository
import javax.inject.Inject


class GetFlashCardsByIdUseCase  @Inject constructor(private val flashCardRepository: FlashCardRepository){

   suspend  operator fun invoke(id: Int): FlashCard {

        return flashCardRepository.getFlashCardsById(id)
    }

}