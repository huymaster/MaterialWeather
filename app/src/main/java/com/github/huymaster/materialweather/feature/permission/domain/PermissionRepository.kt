package com.github.huymaster.materialweather.feature.permission.domain

interface PermissionRepository {
    suspend fun isRequested(permission: String): Boolean
    suspend fun setRequested(permission: String)
    suspend fun removeRequested(permission: String)
}