package com.github.huymaster.materialweather.feature.permission.domain.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface PermissionIcon {
    data class Resource(@DrawableRes val resId: Int) : PermissionIcon
    data class Vector(val vector: ImageVector) : PermissionIcon
}