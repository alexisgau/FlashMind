package com.example.flashmind.domain.usecase.preference

import com.example.flashmind.data.local.ThemePreferenceManager
import javax.inject.Inject

class SaveDarkModeUseCase @Inject constructor(
    private val preferenceManager: ThemePreferenceManager
) {
    suspend operator fun invoke(isDark: Boolean) {
        preferenceManager.saveDarkModeSetting(isDark)
    }
}
