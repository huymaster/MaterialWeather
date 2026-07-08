package com.github.huymaster.materialweather.feature.theme.ui.state

import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType

sealed interface ThemeUiState {
    data object Loading : ThemeUiState
    data class Success(val theme: ThemeType) : ThemeUiState
}