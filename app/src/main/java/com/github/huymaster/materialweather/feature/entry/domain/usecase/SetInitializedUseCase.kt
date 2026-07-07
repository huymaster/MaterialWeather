package com.github.huymaster.materialweather.feature.entry.domain.usecase

import com.github.huymaster.materialweather.core.SuspendUseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource

class SetInitializedUseCase(
    private val appSettingsDataSource: AppSettingsDataSource
) : SuspendUseCase<Boolean, Unit> {
    override suspend fun invoke(input: Boolean): Unit =
        appSettingsDataSource.setInitialized(input)
}