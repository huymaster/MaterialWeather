package com.github.huymaster.materialweather.core.engine

import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

data class Input<I : Any>(
    val metadata: InputMetadata<I>,
    val value: I
) {
    companion object {
        inline operator fun <reified I : Any> invoke(
            name: String,
            value: I
        ) = of(PortMetadata(name), value)

        inline operator fun <reified I : Any> invoke(
            metadata: PortMetadata<I>,
            value: I
        ) = of(metadata, value)

        inline fun <reified I : Any> of(
            metadata: PortMetadata<I>,
            value: I
        ): Input<I> {
            val type = typeOf<I>()
            require(metadata.type.isSubtypeOf(type)) { "Invalid input type" }
            return Input(metadata, value)
        }
    }
}

data class Output<O : Any>(
    val metadata: OutputMetadata<O>,
    val provider: OutputProvider<O>
) {
    companion object {
        inline operator fun <reified O : Any> invoke(
            name: String,
            provider: OutputProvider<O>
        ) = of(PortMetadata(name), provider)

        inline operator fun <reified O : Any> invoke(
            metadata: PortMetadata<O>,
            provider: OutputProvider<O>
        ) = of(metadata, provider)

        inline fun <reified O : Any> of(
            metadata: PortMetadata<O>,
            provider: OutputProvider<O>
        ): Output<O> = Output(metadata, provider)
    }
}

fun interface OutputProvider<O : Any> {
    suspend fun provide(): O
}

fun <I : Any> Input<I>.output(): Output<I> =
    Output(metadata) { value }

suspend fun <O : Any> Output<O>.input(): Input<O> =
    Input(metadata, provider.provide())