package com.github.huymaster.materialweather.feature.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.appSettings by preferencesDataStore("app_settings")

class AppSettingsDataSourceImpl(
    context: Context,
    private val json: Json
) : AppSettingsDataSource {
    private val source = context.appSettings
    private val data = source.data

    override val isInitialized: Flow<Boolean> = data
        .map { it[KEY_INITIALIZED] ?: false }

    override val theme: Flow<ThemeType> = data
        .map {
            val string = it[KEY_THEME] ?: return@map ThemeType.Dynamic.System
            json.decodeFromString(ThemeType.serializer(), string)
        }

    override suspend fun getInitialized(): Boolean =
        source.data.map { it[KEY_INITIALIZED] }.first() ?: true

    override suspend fun setInitialized(isInitialied: Boolean) {
        source.edit { it[KEY_INITIALIZED] = isInitialied }
    }

    override suspend fun getTheme(): ThemeType =
        source.data.map {
            val string = it[KEY_THEME] ?: return@map ThemeType.Dynamic.System
            json.decodeFromString(ThemeType.serializer(), string)
        }.first()

    override suspend fun setTheme(theme: ThemeType) {
        source.edit {
            val string = json.encodeToString(theme)
            it[KEY_THEME] = string
        }
    }

    private companion object {
        val KEY_INITIALIZED = booleanPreferencesKey("initialized")
        val KEY_THEME = stringPreferencesKey("theme")
    }
}