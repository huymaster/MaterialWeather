package com.github.huymaster.materialweather.feature.permission.di

import com.github.huymaster.materialweather.di.KoinProvider
import com.github.huymaster.materialweather.feature.permission.data.PermissionRepositoryImpl
import com.github.huymaster.materialweather.feature.permission.domain.PermissionRepository
import com.github.huymaster.materialweather.feature.permission.ui.PermissionStrategyFactory
import com.github.huymaster.materialweather.feature.permission.ui.viewmodel.PermissionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object PermissionKoinProvider : KoinProvider {
    private val repositoryModule = module {
        singleOf(::PermissionRepositoryImpl) { bind<PermissionRepository>() }
    }
    private val factoryModule = module {
        singleOf(::PermissionStrategyFactory)
    }
    private val viewModelModule = module {
        viewModelOf(::PermissionViewModel)
    }

    override fun getModules(): List<Module> = listOf(
        repositoryModule,
        factoryModule,
        viewModelModule
    )
}