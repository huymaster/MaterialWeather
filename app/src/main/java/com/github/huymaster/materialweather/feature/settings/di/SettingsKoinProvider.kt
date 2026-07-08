package com.github.huymaster.materialweather.feature.settings.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.settings.data.AppSettingsRepositoryImpl
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object SettingsKoinProvider : KoinProvider {
    private val repositoryModule = module {
        singleOf(::AppSettingsRepositoryImpl) { bind<AppSettingsRepository>() }
    }

    override fun getModules(): List<Module> = listOf(
        repositoryModule
    )
}