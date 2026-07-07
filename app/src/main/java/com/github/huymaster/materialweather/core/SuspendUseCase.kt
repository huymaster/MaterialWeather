package com.github.huymaster.materialweather.core

fun interface SuspendUseCase<in I, out O> {
    suspend operator fun invoke(input: I): O
}