package com.github.huymaster.materialweather.di

import com.github.huymaster.materialweather.feature.settings.di.SettingsKoinProvider
import com.github.huymaster.materialweather.feature.theme.di.ThemeKoinProvider
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

object ApplicationKoinProvider : KoinProvider {
    private val coreModule = module {
        single<Json> {
            Json {
                allowTrailingComma = true
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
    }

    private val submodules = listOf(
        SettingsKoinProvider,
        ThemeKoinProvider
    )

    override fun getModules(): List<Module> =
        listOf(coreModule) + submodules.flatMap(KoinProvider::getModules)
}