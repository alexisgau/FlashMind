package com.alexisgau.synapai.domain.usecase.preference

import com.alexisgau.synapai.data.local.preferences.ThemePreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDarkModeUseCase @Inject constructor(
    private val preferenceManager: ThemePreferenceManager,
) {
    operator fun invoke(): Flow<Boolean> = preferenceManager.darkModeFlow
}
