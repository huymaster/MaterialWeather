package com.github.huymaster.materialweather.feature.permission.domain.model

sealed class PermissionType {
    abstract val info: PermissionInfo
    open val minSdk: Int get() = 0
    open val maxSdk: Int get() = Int.MAX_VALUE
    val supportedSdk: IntRange get() = minSdk..maxSdk

    data class Runtime(
        val permission: String,
        override val info: PermissionInfo,
        override val minSdk: Int,
        override val maxSdk: Int
    ) : PermissionType()
}