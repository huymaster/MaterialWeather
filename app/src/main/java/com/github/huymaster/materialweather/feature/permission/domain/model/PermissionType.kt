package com.github.huymaster.materialweather.feature.permission.domain.model

sealed class PermissionType {
    abstract val info: PermissionInfo
    open val minSdk: Int = 0
    open val maxSdk: Int = Int.MAX_VALUE
    open val dependencies: List<PermissionType> = emptyList()

    val supportedSdk: IntRange get() = minSdk..maxSdk

    data class Runtime(
        val permission: String,
        override val info: PermissionInfo,
        override val minSdk: Int,
        override val maxSdk: Int,
        override val dependencies: List<PermissionType> = emptyList()
    ) : PermissionType()
}