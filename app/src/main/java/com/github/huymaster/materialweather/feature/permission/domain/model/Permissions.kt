package com.github.huymaster.materialweather.feature.permission.domain.model

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import com.github.huymaster.materialweather.R

object Permissions {
    val internet = PermissionType.Runtime(
        permission = Manifest.permission.INTERNET,
        info = PermissionInfo(
            labelId = R.string.permission_internet_label,
            descriptionId = R.string.permission_internet_desc,
        )
    )

    val location = PermissionType.Runtime(
        permission = Manifest.permission.ACCESS_COARSE_LOCATION,
        info = PermissionInfo(
            labelId = R.string.permission_location_label,
            descriptionId = R.string.permission_location_desc,
            icon = PermissionIcon.Vector(Icons.Default.LocationOn)
        )
    )

    val locationExact = PermissionType.Runtime(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        info = PermissionInfo(
            labelId = R.string.permission_location_exact_label,
            descriptionId = R.string.permission_location_exact_desc,
            icon = PermissionIcon.Vector(Icons.Default.AddLocationAlt)
        ),
        dependencies = listOf(location)
    )

    @SuppressLint("InlinedApi")
    val notification = PermissionType.Runtime(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        info = PermissionInfo(
            labelId = R.string.permission_notification_label,
            descriptionId = R.string.permission_notification_desc,
            icon = PermissionIcon.Vector(Icons.Default.Notifications)
        ),
        minSdk = Build.VERSION_CODES.TIRAMISU
    )
}