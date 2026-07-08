package com.github.huymaster.materialweather.feature.theme.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import com.github.huymaster.materialweather.feature.theme.domain.usecase.ObserveThemeUseCase
import com.github.huymaster.materialweather.feature.theme.domain.usecase.SetThemeUseCase
import com.github.huymaster.materialweather.feature.theme.ui.state.ThemeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val observeThemeUseCase: ObserveThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {

    val themeUiState: StateFlow<ThemeUiState> = observeThemeUseCase(Unit)
        .map { theme -> ThemeUiState.Success(theme) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeUiState.Loading
        )

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch { setThemeUseCase(theme) }
    }
}