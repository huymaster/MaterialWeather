package com.github.huymaster.materialweather.feature.theme.domain.model

sealed interface ThemeUiState {
    data object Loading : ThemeUiState
    data class Success(val theme: ThemeType) : ThemeUiState
}