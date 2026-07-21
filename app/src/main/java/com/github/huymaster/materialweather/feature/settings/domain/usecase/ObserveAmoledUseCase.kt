package com.github.huymaster.materialweather.feature.settings.domain.usecase

import com.github.huymaster.materialweather.core.UseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveAmoledUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : UseCase<Unit, Flow<Boolean>> {
    override fun invoke(input: Unit): Flow<Boolean> =
        appSettingsRepository.isAmoled
}