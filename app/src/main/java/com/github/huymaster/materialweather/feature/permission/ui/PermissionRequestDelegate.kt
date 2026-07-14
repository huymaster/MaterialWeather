package com.github.huymaster.materialweather.feature.permission.ui

class PermissionRequestDelegate(
    private val requestPermissionAction: ((String) -> Unit)? = null,
    private val openAppDetailsSettingsAction: ((String) -> Unit)? = null
) {
    fun requestRuntimePermission(permission: String) {
        requestPermissionAction?.invoke(permission)
    }

    fun openAppDetailsSettings(packageName: String) {
        openAppDetailsSettingsAction?.invoke(packageName)
    }
}