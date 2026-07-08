package com.github.huymaster.materialweather.feature.permission.ui.strategy

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionState
import com.github.huymaster.materialweather.feature.permission.ui.PermissionStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlwaysGrantedPermissionStrategy : PermissionStrategy<Unit> {
    override val state: StateFlow<PermissionState> =
        MutableStateFlow(PermissionState.GRANTED).asStateFlow()

    override suspend fun requestPermission(
        context: Context,
        launcher: ActivityResultLauncher<Unit>?
    ) {
        // No-op
    }

    override suspend fun checkPermission(context: Context) {
        // No-op
    }
}