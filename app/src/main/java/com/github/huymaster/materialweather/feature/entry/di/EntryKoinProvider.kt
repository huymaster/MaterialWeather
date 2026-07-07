package com.github.huymaster.materialweather.feature.entry.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.entry.domain.usecase.GetInitializedUseCase
import com.github.huymaster.materialweather.feature.entry.domain.usecase.ObserveInitializedUseCase
import com.github.huymaster.materialweather.feature.entry.domain.usecase.SetInitializedUseCase
import com.github.huymaster.materialweather.feature.entry.presentation.viewmodel.EntryViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object EntryKoinProvider : KoinProvider {
    private val useCaseModule = module {
        factoryOf(::GetInitializedUseCase)
        factoryOf(::SetInitializedUseCase)
        factoryOf(::ObserveInitializedUseCase)
    }
    private val viewModelModule = module {
        viewModelOf(::EntryViewModel)
    }

    override fun getModules(): List<Module> = listOf(
        useCaseModule,
        viewModelModule
    )
}