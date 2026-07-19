package com.github.huymaster.materialweather.feature.entry.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.entry.ui.viewmodel.EntryViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object EntryKoinProvider : KoinProvider {
    private val viewModelModule = module {
        viewModelOf(::EntryViewModel)
    }

    override fun getModules(): List<Module> = listOf(
        viewModelModule
    )
}