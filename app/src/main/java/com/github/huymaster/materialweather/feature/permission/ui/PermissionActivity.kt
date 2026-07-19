package com.github.huymaster.materialweather.feature.permission.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.huymaster.materialweather.core.BaseActivity
import com.github.huymaster.materialweather.feature.permission.ui.viewmodel.PermissionViewModel
import org.koin.compose.viewmodel.koinViewModel

class PermissionActivity : BaseActivity() {
    @Composable
    override fun Content(adaptiveInfo: WindowAdaptiveInfo) {
        val viewModel = koinViewModel<PermissionViewModel>()
        val requiredPermissions by viewModel.requiredPermissions.collectAsStateWithLifecycle()
        val optionalPermissions by viewModel.optionalPermissions.collectAsStateWithLifecycle()
        val delegate = rememberPermissionDelegate()

        val requiredControllers = requiredPermissions.map { permission ->
            rememberPermission(permission, delegate)
        }
        val optionalControllers = optionalPermissions.map { permission ->
            rememberPermission(permission, delegate)
        }

        Scaffold { paddingValues ->
            PermissionScreen(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                adaptiveInfo = adaptiveInfo,
                requiredPermissions = requiredControllers,
                optionalPermissions = optionalControllers,
                onComplete = {
                    viewModel.markInitialized()
                    moveToMain()
                }
            )
        }
    }

    private fun moveToMain() {
        TODO()
    }
}