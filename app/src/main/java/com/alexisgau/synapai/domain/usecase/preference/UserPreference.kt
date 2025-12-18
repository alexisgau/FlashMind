package com.alexisgau.synapai.domain.usecase.preference

import com.alexisgau.synapai.data.local.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPreferencesUseCase @Inject constructor(
    private val preferenceManager: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<Boolean> = preferenceManager.isOnboardingCompleted
}


class SavePreferencesUseCase @Inject constructor(
    private val preferenceManager: UserPreferencesRepository,
) {
    suspend operator fun invoke(isCompleted: Boolean) {
        preferenceManager.setOnboardingCompleted(isCompleted)
    }
}