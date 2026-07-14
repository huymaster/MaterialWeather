package com.github.huymaster.materialweather.feature.permission.ui

import android.os.Build
import com.github.huymaster.materialweather.feature.permission.domain.PermissionRepository
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionType
import com.github.huymaster.materialweather.feature.permission.ui.strategy.AlwaysGrantedPermissionStrategy
import com.github.huymaster.materialweather.feature.permission.ui.strategy.RuntimePermissionStrategy

class PermissionStrategyFactory(
    private val permissionRepository: PermissionRepository
) {
    fun create(permissionType: PermissionType): PermissionStrategy<*> {
        return when (permissionType) {
            is PermissionType.Runtime -> {
                if (Build.VERSION.SDK_INT in permissionType.supportedSdk)
                    RuntimePermissionStrategy(permissionType.permission, permissionRepository)
                else
                    AlwaysGrantedPermissionStrategy()
            }
        }
    }
}