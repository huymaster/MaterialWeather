package com.github.huymaster.materialweather.feature.entry.domain.model

sealed interface EntryNavigationRoute {
    data object Initial : EntryNavigationRoute
    data object MoveToInit : EntryNavigationRoute
    data object MoveToMain : EntryNavigationRoute
}