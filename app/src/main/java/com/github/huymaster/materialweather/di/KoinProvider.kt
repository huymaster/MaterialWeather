package com.github.huymaster.materialweather.di

import org.koin.core.module.Module

fun interface KoinProvider {
    fun getModules(): List<Module>
}