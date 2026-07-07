package com.github.huymaster.materialweather.core

fun interface UseCase<in I, out O> {
    operator fun invoke(input: I): O
}