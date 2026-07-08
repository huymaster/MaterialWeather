package com.github.huymaster.materialweather.feature.permission.ui.strategy

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.huymaster.materialweather.feature.permission.domain.PermissionRepository
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionState
import com.github.huymaster.materialweather.feature.permission.ui.PermissionStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RuntimePermissionStrategy(
    private val permission: String,
    private val permissionRepository: PermissionRepository
) : PermissionStrategy<String> {
    private val _state = MutableStateFlow(PermissionState.DENIED)
    override val state: StateFlow<PermissionState> = _state.asStateFlow()

    override suspend fun requestPermission(
        context: Context,
        launcher: ActivityResultLauncher<String>?
    ) {
        permissionRepository.setRequested(permission)
        launcher?.launch(permission)
    }

    override suspend fun checkPermission(context: Context) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            _state.value = PermissionState.GRANTED
            return
        }

        val showRationale = if (context is Activity) {
            ActivityCompat.shouldShowRequestPermissionRationale(context, permission)
        } else {
            false
        }

        val wasRequested = permissionRepository.isRequested(permission)

        _state.value = if (!showRationale && wasRequested) {
            PermissionState.PERMANENTLY_DENIED
        } else {
            PermissionState.DENIED
        }
    }
}