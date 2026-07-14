package com.github.huymaster.materialweather.feature.permission.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionIcon
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionState
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionType
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun rememberPermissionDelegate(onPermissionResults: (Boolean) -> Unit = {}): PermissionRequestDelegate {
    val context = LocalContext.current

    val runtimePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onPermissionResults
    )

    return remember(context) {
        PermissionRequestDelegate(
            requestPermissionAction = { permission -> runtimePermissionLauncher.launch(permission) },
            openAppDetailsSettingsAction = { packageName ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun rememberPermission(
    permission: PermissionType,
    delegate: PermissionRequestDelegate
): PermissionController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val factory = koinInject<PermissionStrategyFactory>()
    val strategy = remember(permission) { factory.create(permission) }

    val dependencyControllers = permission.dependencies.map { dep ->
        key(dep) { rememberPermission(permission = dep, delegate = delegate) }
    }

    val selfState by strategy.state.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner, strategy, context) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            strategy.checkPermission(context)
        }
    }

    val ungrantedDependencies by remember {
        derivedStateOf {
            dependencyControllers.filter { it.state != PermissionState.GRANTED }
        }
    }

    val aggregateState by remember {
        derivedStateOf {
            when {
                ungrantedDependencies.any { it.state == PermissionState.PERMANENTLY_DENIED } -> PermissionState.PERMANENTLY_DENIED
                ungrantedDependencies.isNotEmpty() -> PermissionState.DENIED
                else -> selfState
            }
        }
    }

    val onCheck: () -> Unit = remember(strategy, context) {
        {
            coroutineScope.launch { strategy.checkPermission(context) }
        }
    }

    val onRequest: (PermissionMissingCallback) -> Unit = remember(strategy, context, delegate) {
        { callback ->
            if (ungrantedDependencies.isEmpty()) {
                coroutineScope.launch { strategy.requestPermission(context, delegate) }
            } else {
                callback.onPermissionMissing(ungrantedDependencies.map { it.type })
            }
        }
    }

    return remember(permission, aggregateState, ungrantedDependencies, onRequest, onCheck) {
        PermissionController(
            type = permission,
            label = permission.info.labelId,
            description = permission.info.descriptionId,
            icon = permission.info.icon,
            state = aggregateState,
            ungrantedDependencies = ungrantedDependencies,
            check = onCheck,
            request = onRequest
        )
    }
}

data class PermissionController(
    val type: PermissionType,
    val label: Int,
    val description: Int? = null,
    val icon: PermissionIcon? = null,
    val state: PermissionState,
    val ungrantedDependencies: List<PermissionController>,
    val check: () -> Unit,
    val request: (PermissionMissingCallback) -> Unit
)

fun interface PermissionMissingCallback {
    fun onPermissionMissing(missingPermissions: List<PermissionType>)
}