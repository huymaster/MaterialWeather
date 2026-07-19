package com.github.huymaster.materialweather.feature.permission.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionType
import com.github.huymaster.materialweather.feature.permission.domain.model.Permissions
import com.github.huymaster.materialweather.feature.settings.domain.usecase.SetInitializedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val setInitializedUseCase: SetInitializedUseCase
) : ViewModel() {
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

    fun markInitialized() {
        viewModelScope.launch { setInitializedUseCase(true) }
    }
}