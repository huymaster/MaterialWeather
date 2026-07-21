package com.github.huymaster.materialweather.core.engine

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

typealias InputMetadata<I> = PortMetadata<I>
typealias OutputMetadata<O> = PortMetadata<O>

data class PortMetadata<T : Any>(
    val name: String,
    val type: KType,
    val clazz: KClass<T>
) {
    companion object {
        inline operator fun <reified T : Any> invoke(name: String) =
            of<T>(name)

        inline fun <reified T : Any> of(name: String) =
            PortMetadata(name, typeOf<T>(), T::class)
    }
}