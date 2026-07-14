package com.github.huymaster.materialweather.feature.permission.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionType
import com.github.huymaster.materialweather.feature.permission.domain.model.Permissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionViewModel : ViewModel() {
    private val _requiredPermissions = MutableStateFlow(
        listOf<PermissionType>(
            Permissions.internet,
            Permissions.location
        )
    )

    private val _optionalPermissions = MutableStateFlow(
        listOf<PermissionType>(
            Permissions.locationExact,
            Permissions.notification
        )
    )

    val requiredPermissions: StateFlow<List<PermissionType>> = _requiredPermissions.asStateFlow()
    val optionalPermissions: StateFlow<List<PermissionType>> = _optionalPermissions.asStateFlow()
}