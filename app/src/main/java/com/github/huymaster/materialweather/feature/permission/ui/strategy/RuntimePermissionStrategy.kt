package com.github.huymaster.materialweather.feature.permission.ui.strategy

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.huymaster.materialweather.core.utils.findActivity
import com.github.huymaster.materialweather.feature.permission.domain.PermissionRepository
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionState
import com.github.huymaster.materialweather.feature.permission.ui.PermissionRequestDelegate
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
        delegate: PermissionRequestDelegate
    ) {
        permissionRepository.setRequested(permission)
        delegate.requestRuntimePermission(permission)
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

        val activity = context.findActivity()
        val showRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
        } ?: false

        val wasRequested = permissionRepository.isRequested(permission)

        _state.value = if (!showRationale && wasRequested) {
            PermissionState.PERMANENTLY_DENIED
        } else {
            PermissionState.DENIED
        }
    }
}