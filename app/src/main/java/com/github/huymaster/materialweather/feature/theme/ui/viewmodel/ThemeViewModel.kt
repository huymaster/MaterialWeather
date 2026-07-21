package com.github.huymaster.materialweather.feature.theme.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.huymaster.materialweather.feature.settings.domain.usecase.ObserveAmoledUseCase
import com.github.huymaster.materialweather.feature.settings.domain.usecase.SetAmoledUseCase
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
    observeThemeUseCase: ObserveThemeUseCase,
    observeAmoledUseCase: ObserveAmoledUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setAmoledUseCase: SetAmoledUseCase
) : ViewModel() {

    val themeUiState: StateFlow<ThemeUiState> = observeThemeUseCase(Unit)
        .map { theme -> ThemeUiState.Success(theme) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeUiState.Loading
        )

    val isAmoled: StateFlow<Boolean> = observeAmoledUseCase(Unit)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch { setThemeUseCase(theme) }
    }

    fun setAmoled(isAmoled: Boolean) {
        viewModelScope.launch { setAmoledUseCase(isAmoled) }
    }
}