package com.github.huymaster.materialweather.feature.entry.domain.usecase

import com.github.huymaster.materialweather.core.UseCase
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource
import kotlinx.coroutines.flow.Flow

class ObserveInitializedUseCase(
    private val appSettingsDataSource: AppSettingsDataSource
) : UseCase<Unit, Flow<Boolean>> {
    override fun invoke(input: Unit): Flow<Boolean> =
        appSettingsDataSource.isInitialized
}