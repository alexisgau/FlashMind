package com.example.flashmind.domain.usecase

import com.example.flashmind.data.local.ThemePreferenceManager
import javax.inject.Inject

class SaveDarkModeUseCase @Inject constructor(
    private val preferenceManager: ThemePreferenceManager
) {
    suspend operator fun invoke(isDark: Boolean) {
        preferenceManager.saveDarkModeSetting(isDark)
    }
}
