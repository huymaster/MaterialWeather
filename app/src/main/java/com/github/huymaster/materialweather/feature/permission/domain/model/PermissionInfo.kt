package com.github.huymaster.materialweather.feature.permission.domain.model

import androidx.annotation.StringRes

data class PermissionInfo(
    @StringRes val labelId: Int,
    @StringRes val descriptionId: Int? = null,
    val icon: PermissionIcon? = null,
)