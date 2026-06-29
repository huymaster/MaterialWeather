package com.github.huymaster.materialweather.feature.theme.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.theme.presentation.ThemeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object ThemeKoinProvider : KoinProvider {
    private val viewModelModule = module {
        viewModelOf(::ThemeViewModel)
    }

    override fun getModules(): List<Module> = listOf(
        viewModelModule
    )
}