package com.github.huymaster.materialweather.feature.entry.domain.usecase

import com.github.huymaster.materialweather.core.SuspendUseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource

class GetInitializedUseCase(
    private val appSettingsDataSource: AppSettingsDataSource
) : SuspendUseCase<Unit, Boolean> {
    override suspend fun invoke(input: Unit): Boolean =
        appSettingsDataSource.getInitialized()
}