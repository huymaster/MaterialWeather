package com.github.huymaster.materialweather.feature.permission.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.github.huymaster.materialweather.feature.permission.domain.PermissionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

private val Context.dataStore by preferencesDataStore("permission")

class PermissionRepositoryImpl(context: Context) : PermissionRepository {
    private val store = context.dataStore

    override suspend fun isRequested(permission: String): Boolean {
        val prefKey = getPermissionKey(permission)
        return store.data
            .map { preferences -> preferences[prefKey] == true }
            .first()
    }

    override suspend fun setRequested(permission: String) {
        val prefKey = getPermissionKey(permission)
        store.edit { preferences -> preferences[prefKey] = true }
    }

    override suspend fun removeRequested(permission: String) {
        val prefKey = getPermissionKey(permission)
        store.edit { preferences -> preferences.remove(prefKey) }
    }

    companion object {
        private val keyCache = ConcurrentHashMap<String, Preferences.Key<Boolean>>()

        fun getPermissionKey(permission: String): Preferences.Key<Boolean> =
            keyCache.getOrPut(permission) { booleanPreferencesKey("~:$permission:~") }
    }
}