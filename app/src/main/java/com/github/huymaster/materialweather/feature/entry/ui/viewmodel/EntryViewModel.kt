package com.github.huymaster.materialweather.feature.entry.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.huymaster.materialweather.feature.entry.ui.state.EntryNavigationState
import com.github.huymaster.materialweather.feature.settings.domain.usecase.GetInitializedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EntryViewModel(
    private val getInitializedUseCase: GetInitializedUseCase
) : ViewModel() {
    private val _entryState = MutableStateFlow<EntryNavigationState>(EntryNavigationState.Initial)
    val entryState = _entryState.asStateFlow()

    fun check() {
        viewModelScope.launch {
            if (getInitializedUseCase(Unit))
                _entryState.value = EntryNavigationState.MoveToMain
            else
                _entryState.value = EntryNavigationState.MoveToInit
        }
    }
}