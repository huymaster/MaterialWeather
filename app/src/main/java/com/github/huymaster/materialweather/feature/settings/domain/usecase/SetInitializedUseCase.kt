package com.github.huymaster.materialweather.feature.settings.domain.usecase

import com.github.huymaster.materialweather.core.SuspendUseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository

class SetInitializedUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : SuspendUseCase<Boolean, Unit> {
    override suspend fun invoke(input: Boolean) =
        appSettingsRepository.setInitialized(input)
}