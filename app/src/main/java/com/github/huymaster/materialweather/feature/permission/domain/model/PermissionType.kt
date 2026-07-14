package com.github.huymaster.materialweather.feature.permission.domain.model

sealed class PermissionType {
    abstract val info: PermissionInfo
    abstract val minSdk: Int
    abstract val maxSdk: Int
    open val dependencies: List<PermissionType> = emptyList()

    val supportedSdk: IntRange get() = minSdk..maxSdk

    data class Runtime(
        val permission: String,
        override val info: PermissionInfo,
        override val minSdk: Int = 0,
        override val maxSdk: Int = Int.MAX_VALUE,
        override val dependencies: List<PermissionType> = emptyList()
    ) : PermissionType()
}