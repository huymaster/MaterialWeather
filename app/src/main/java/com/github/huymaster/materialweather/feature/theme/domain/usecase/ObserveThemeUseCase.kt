package com.github.huymaster.materialweather.feature.theme.domain.usecase

import com.github.huymaster.materialweather.core.UseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import kotlinx.coroutines.flow.Flow

class ObserveThemeUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : UseCase<Unit, Flow<ThemeType>> {
    override fun invoke(input: Unit): Flow<ThemeType> =
        appSettingsRepository.theme
}