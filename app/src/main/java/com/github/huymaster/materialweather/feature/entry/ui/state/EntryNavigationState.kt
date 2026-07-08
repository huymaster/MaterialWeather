package com.github.huymaster.materialweather.feature.entry.ui.state

sealed interface EntryNavigationState {
    data object Initial : EntryNavigationState
    data object MoveToInit : EntryNavigationState
    data object MoveToMain : EntryNavigationState
}