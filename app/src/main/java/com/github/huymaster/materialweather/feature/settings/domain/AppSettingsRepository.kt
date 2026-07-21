package com.github.huymaster.materialweather.feature.settings.domain

import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val isInitialized: Flow<Boolean>
    val isAmoled: Flow<Boolean>
    val theme: Flow<ThemeType>


    suspend fun getInitialized(): Boolean
    suspend fun setInitialized(isInitialied: Boolean)

    suspend fun getAmoled(): Boolean
    suspend fun setAmoled(isAmoled: Boolean)


    suspend fun getTheme(): ThemeType
    suspend fun setTheme(theme: ThemeType)
}