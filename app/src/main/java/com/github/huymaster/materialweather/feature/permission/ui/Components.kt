package com.github.huymaster.materialweather.feature.permission.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionIcon
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionState
import com.github.huymaster.materialweather.feature.permission.domain.model.PermissionType

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    adaptiveInfo: WindowAdaptiveInfo,
    requiredPermissions: List<PermissionController>,
    optionalPermissions: List<PermissionController>,
    onComplete: () -> Unit
) {
    val isAllRequiredGranted = requiredPermissions.all { it.state == PermissionState.GRANTED }
    val isExpandedSize =
        adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Column(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.permission_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.permission_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (!isExpandedSize) {
                SingleColumnPermissionPane(requiredPermissions, optionalPermissions)
            } else {
                DoubleColumnPermissionPane(requiredPermissions, optionalPermissions)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                modifier = Modifier.align(Alignment.CenterEnd),
                enabled = isAllRequiredGranted,
                onClick = onComplete,
            ) {
                Text(
                    text = stringResource(R.string.permission_button_continue),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SingleColumnPermissionPane(
    requiredPermissions: List<PermissionController>,
    optionalPermissions: List<PermissionController>
) {
    val requiredTitle = stringResource(R.string.permission_required)
    val optionalTitle = stringResource(R.string.permission_optional)

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (requiredPermissions.isNotEmpty()) {
            permissionListGroup(requiredTitle, requiredPermissions)
        }
        if (optionalPermissions.isNotEmpty()) {
            permissionListGroup(optionalTitle, optionalPermissions)
        }
    }
}

@Composable
private fun DoubleColumnPermissionPane(
    requiredPermissions: List<PermissionController>,
    optionalPermissions: List<PermissionController>
) {
    val requiredTitle = stringResource(R.string.permission_required)
    val optionalTitle = stringResource(R.string.permission_optional)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (requiredPermissions.isNotEmpty()) {
                permissionListGroup(requiredTitle, requiredPermissions)
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (optionalPermissions.isNotEmpty()) {
                permissionListGroup(optionalTitle, optionalPermissions)
            }
        }
    }
}

private fun LazyListScope.permissionListGroup(
    title: String,
    permissions: List<PermissionController>
) {
    item {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp)
        )
    }
    items(permissions.size, key = { permissions[it].type.toString() }) { index ->
        PermissionItem(
            permission = permissions[index]
        )
    }
}

@Composable
private fun PermissionItem(
    permission: PermissionController,
    modifier: Modifier = Modifier
) {
    var missingPermissions by remember { mutableStateOf(emptyList<PermissionType>()) }

    // 1. Tối ưu hóa màu sắc chạy đồng bộ theo trạng thái Card
    val containerColor by animateColorAsState(
        targetValue = when (permission.state) {
            PermissionState.GRANTED -> MaterialTheme.colorScheme.primaryContainer
            PermissionState.PERMANENTLY_DENIED -> MaterialTheme.colorScheme.errorContainer
            PermissionState.DENIED -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "containerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when (permission.state) {
            PermissionState.GRANTED -> MaterialTheme.colorScheme.onPrimaryContainer
            PermissionState.PERMANENTLY_DENIED -> MaterialTheme.colorScheme.onErrorContainer
            PermissionState.DENIED -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "contentColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300)),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                PermissionIconRenderer(
                    icon = permission.icon,
                    tint = contentColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(permission.label),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                permission.description?.let { descId ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(descId),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (missingPermissions.isNotEmpty()) {
                    val resource = LocalResources.current
                    val missingLabel = remember(missingPermissions) {
                        missingPermissions.map { it.info.labelId }
                    }
                    val missingPermissionsLabel =
                        missingLabel.joinToString { resource.getString(it) }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.permission_missing_dependencies,
                            missingPermissionsLabel
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            AnimatedContent(
                targetState = permission.state,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                            fadeOut(animationSpec = tween(90))
                },
                label = "actionButton"
            ) { state ->
                when (state) {
                    PermissionState.GRANTED -> {
                        Box(
                            modifier = Modifier.sizeIn(
                                minWidth = 80.dp,
                                minHeight = 40.dp
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    PermissionState.DENIED, PermissionState.PERMANENTLY_DENIED -> {
                        OutlinedButton(
                            onClick = {
                                permission.request { missingPermissions = it }
                            }
                        ) {
                            if (state == PermissionState.DENIED) {
                                Text(stringResource(R.string.permission_button_grant))
                            } else {
                                Icon(
                                    Icons.Rounded.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionIconRenderer(
    icon: PermissionIcon?,
    tint: Color,
    modifier: Modifier = Modifier
) {
    when (icon) {
        is PermissionIcon.Resource -> {
            Icon(
                painter = painterResource(id = icon.resId),
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(24.dp)
            )
        }

        is PermissionIcon.Vector -> {
            Icon(
                imageVector = icon.vector,
                contentDescription = null,
                tint = tint,
                modifier = modifier.size(24.dp)
            )
        }

        null -> Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = tint,
            modifier = modifier.size(24.dp)
        )
    }
}