package com.example.flashmind.domain.usecase

import com.example.flashmind.data.local.ThemePreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val preferenceManager: ThemePreferenceManager
) {
    operator fun invoke(): Flow<Boolean> = preferenceManager.darkModeFlow
}
