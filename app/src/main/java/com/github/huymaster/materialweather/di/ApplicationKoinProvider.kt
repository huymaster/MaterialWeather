package com.github.huymaster.materialweather.di

import org.koin.core.module.Module
import org.koin.dsl.module

object ApplicationKoinProvider : KoinProvider {
    private val coreModule = module {
    }

    private val submodules = listOf<KoinProvider>(
    )

    override fun getModules(): List<Module> =
        listOf(coreModule) + submodules.flatMap(KoinProvider::getModules)
}