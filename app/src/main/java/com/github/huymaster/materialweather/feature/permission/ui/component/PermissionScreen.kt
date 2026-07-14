package com.github.huymaster.materialweather.feature.permission.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.github.huymaster.materialweather.feature.permission.ui.PermissionController

@Composable
fun PermissionScreen(
    modifier: Modifier,
    adaptiveInfo: WindowAdaptiveInfo,
    requiredPermissions: List<PermissionController>,
    optionalPermissions: List<PermissionController>
) {
    val isExpandedSize =
        adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Box(modifier) {

    }
}