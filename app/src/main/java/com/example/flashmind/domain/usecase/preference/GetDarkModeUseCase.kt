package com.example.flashmind.domain.usecase.preference

import com.example.flashmind.data.local.preferences.ThemePreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val preferenceManager: ThemePreferenceManager
) {
    operator fun invoke(): Flow<Boolean> = preferenceManager.darkModeFlow
}
