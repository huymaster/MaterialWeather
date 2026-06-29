package com.github.huymaster.materialweather.feature.theme.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val appSettingsDataSource: AppSettingsDataSource
) : ViewModel() {
    val themeUiState = appSettingsDataSource.theme
        .map { theme -> ThemeUiState.Success(theme) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ThemeUiState.Loading
        )

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch { appSettingsDataSource.setTheme(theme) }
    }
}