package com.github.huymaster.materialweather.feature.theme.domain.usecase

import com.github.huymaster.materialweather.core.SuspendUseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType

class GetThemeUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : SuspendUseCase<Unit, ThemeType> {
    override suspend fun invoke(input: Unit): ThemeType =
        appSettingsRepository.getTheme()
}