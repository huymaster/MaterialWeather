package com.github.huymaster.materialweather.feature.theme.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.theme.domain.usecase.GetThemeUseCase
import com.github.huymaster.materialweather.feature.theme.domain.usecase.ObserveThemeUseCase
import com.github.huymaster.materialweather.feature.theme.domain.usecase.SetThemeUseCase
import com.github.huymaster.materialweather.feature.theme.ui.viewmodel.ThemeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object ThemeKoinProvider : KoinProvider {
    private val useCaseModule = module {
        factoryOf(::GetThemeUseCase)
        factoryOf(::SetThemeUseCase)
        factoryOf(::ObserveThemeUseCase)
    }

    private val viewModelModule = module {
        viewModelOf(::ThemeViewModel)
    }

    override fun getModules(): List<Module> = listOf(
        useCaseModule,
        viewModelModule
    )
}