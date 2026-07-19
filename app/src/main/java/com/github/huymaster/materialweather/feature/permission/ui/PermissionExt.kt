package com.github.huymaster.materialweather.feature.permission.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
fun rememberPermissionDelegate(
    onPermissionResults: (Boolean) -> Unit = {}
): PermissionRequestDelegate {
    val context = LocalContext.current

    val currentOnResult by rememberUpdatedState(onPermissionResults)

    val runtimePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { currentOnResult(it) }
    )

    return remember(context) {
        PermissionRequestDelegate(
            requestPermissionAction = { permission -> runtimePermissionLauncher.launch(permission) },
            openAppDetailsSettingsAction = { packageName ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun rememberPermission(
    permission: PermissionType,
    delegate: PermissionRequestDelegate,
    factory: PermissionStrategyFactory = koinInject()
): PermissionController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val strategy = remember(permission, factory) { factory.create(permission) }

    val dependencyControllers = permission.dependencies.map { dep ->
        rememberPermission(permission = dep, delegate = delegate, factory = factory)
    }

    val selfState by strategy.state.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner, strategy, context) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            strategy.checkPermission(context)
        }
    }

    val ungrantedDependencies = remember(dependencyControllers) {
        dependencyControllers.filter { it.state != PermissionState.GRANTED }
    }

    val aggregateState = remember(selfState, ungrantedDependencies) {
        when {
            ungrantedDependencies.any { it.state == PermissionState.PERMANENTLY_DENIED } -> PermissionState.PERMANENTLY_DENIED
            ungrantedDependencies.isNotEmpty() -> PermissionState.DENIED
            else -> selfState
        }
    }

    val currentDelegate by rememberUpdatedState(delegate)

    val onCheck: () -> Unit = remember(strategy, context, coroutineScope) {
        {
            coroutineScope.launch { strategy.checkPermission(context) }
        }
    }

    val onRequest: (PermissionMissingCallback) -> Unit = remember(
        strategy,
        context,
        coroutineScope,
        ungrantedDependencies
    ) {
        { callback ->
            if (ungrantedDependencies.isEmpty()) {
                coroutineScope.launch { strategy.requestPermission(context, currentDelegate) }
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