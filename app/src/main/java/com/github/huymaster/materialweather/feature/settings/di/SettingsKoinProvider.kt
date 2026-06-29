package com.github.huymaster.materialweather.feature.settings.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.settings.data.AppSettingsDataSourceImpl
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object SettingsKoinProvider : KoinProvider {
    private val dataSourceModule = module {
        singleOf(::AppSettingsDataSourceImpl) { bind<AppSettingsDataSource>() }
    }

    override fun getModules(): List<Module> = listOf(
        dataSourceModule
    )
}