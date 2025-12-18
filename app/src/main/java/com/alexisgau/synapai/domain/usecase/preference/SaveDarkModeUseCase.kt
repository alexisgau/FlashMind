package com.alexisgau.synapai.domain.usecase.preference

import com.alexisgau.synapai.data.local.preferences.ThemePreferenceManager
import javax.inject.Inject

class SaveDarkModeUseCase @Inject constructor(
    private val preferenceManager: ThemePreferenceManager,
) {
    suspend operator fun invoke(isDark: Boolean) {
        preferenceManager.saveDarkModeSetting(isDark)
    }
}
