package com.github.huymaster.materialweather.feature.entry.presentation.state

sealed interface EntryNavigationState {
    data object Initial : EntryNavigationState
    data object MoveToInit : EntryNavigationState
    data object MoveToMain : EntryNavigationState
}