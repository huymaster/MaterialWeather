package com.github.huymaster.materialweather.feature.theme.domain.usecase

import com.github.huymaster.materialweather.core.SuspendUseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType

class SetThemeUseCase(
    private val appSettingsDataSource: AppSettingsDataSource
) : SuspendUseCase<ThemeType, Unit> {
    override suspend fun invoke(input: ThemeType): Unit =
        appSettingsDataSource.setTheme(input)
}