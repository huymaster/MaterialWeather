package com.github.huymaster.materialweather.feature.settings.domain.usecase

import com.github.huymaster.materialweather.core.SuspendUseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository

class GetAmoledUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : SuspendUseCase<Unit, Boolean> {
    override suspend fun invoke(input: Unit): Boolean =
        appSettingsRepository.getAmoled()
}