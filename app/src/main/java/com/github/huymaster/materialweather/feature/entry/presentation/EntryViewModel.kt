package com.github.huymaster.materialweather.feature.entry.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.huymaster.materialweather.feature.entry.domain.model.EntryNavigationRoute
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EntryViewModel(
    private val appSettingsDataSource: AppSettingsDataSource
) : ViewModel() {
    private val _entryState = MutableStateFlow<EntryNavigationRoute>(EntryNavigationRoute.Initial)
    val entryState = _entryState.asStateFlow()

    fun check() {
        viewModelScope.launch {
            if (appSettingsDataSource.getInitialized())
                _entryState.value = EntryNavigationRoute.MoveToMain
            else
                _entryState.value = EntryNavigationRoute.MoveToInit
        }
    }
}