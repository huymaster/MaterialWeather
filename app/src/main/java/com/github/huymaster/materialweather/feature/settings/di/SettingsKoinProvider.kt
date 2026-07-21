package com.github.huymaster.materialweather.feature.settings.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.settings.data.AppSettingsRepositoryImpl
import com.github.huymaster.materialweather.feature.settings.domain.AppSettingsRepository
import com.github.huymaster.materialweather.feature.settings.domain.usecase.GetAmoledUseCase
import com.github.huymaster.materialweather.feature.settings.domain.usecase.GetInitializedUseCase
import com.github.huymaster.materialweather.feature.settings.domain.usecase.ObserveAmoledUseCase
import com.github.huymaster.materialweather.feature.settings.domain.usecase.ObserveInitializedUseCase
import com.github.huymaster.materialweather.feature.settings.domain.usecase.SetAmoledUseCase
import com.github.huymaster.materialweather.feature.settings.domain.usecase.SetInitializedUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object SettingsKoinProvider : KoinProvider {
    private val repositoryModule = module {
        singleOf(::AppSettingsRepositoryImpl) { bind<AppSettingsRepository>() }
    }

    private val useCaseModule = module {
        factoryOf(::GetInitializedUseCase)
        factoryOf(::SetInitializedUseCase)
        factoryOf(::ObserveInitializedUseCase)
        factoryOf(::GetAmoledUseCase)
        factoryOf(::SetAmoledUseCase)
        factoryOf(::ObserveAmoledUseCase)
    }

    override fun getModules(): List<Module> = listOf(
        repositoryModule,
        useCaseModule
    )
}