package com.github.huymaster.materialweather.feature.permission.ui

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionState
import kotlinx.coroutines.flow.StateFlow

interface PermissionStrategy<T> {
    val state: StateFlow<PermissionState>

    suspend fun requestPermission(context: Context, launcher: ActivityResultLauncher<T>? = null)
    suspend fun checkPermission(context: Context)
}