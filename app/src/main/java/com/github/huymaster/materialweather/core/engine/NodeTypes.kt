package com.github.huymaster.materialweather.core.engine

import kotlin.reflect.KClass
import kotlin.uuid.Uuid

sealed class NodeParam<T : Any>(
    val id: String = Uuid.random().toString(),
    val name: String,
    val type: KClass<T>
) {
    class Input<T : Any>(
        name: String,
        type: KClass<T>,
        val defaultValue: T? = null,
        val isOptional: Boolean = defaultValue != null
    ) : NodeParam<T>(name = name, type = type)

    class Output<T : Any>(
        name: String,
        type: KClass<T>
    ) : NodeParam<T>(name = name, type = type)

    companion object {
        inline fun <reified T : Any> input(
            name: String,
            defaultValue: T? = null,
            isOptional: Boolean = defaultValue != null
        ) = Input(name, T::class, defaultValue, isOptional)

        inline fun <reified T : Any> output(
            name: String
        ) = Output(name, T::class)
    }
}

sealed interface NullSafeValue<out T : Any> {
    val type: KClass<out T>

    data object NullValue : NullSafeValue<Nothing> {
        override val type = Nothing::class
    }

    data class Value<T : Any>(val value: T) : NullSafeValue<T> {
        override val type = value::class
    }

    companion object {
        fun <T : Any> of(value: T?): NullSafeValue<T> {
            if (value == null) return NullValue
            return Value(value)
        }
    }
}

data class ParamConnection(
    val fromParamId: String,
    val toParamId: String
)

enum class NodeStatus {
    IDLE,
    RUNNING,
    COMPLETED,
    FAILED
}

fun interface NodeExecutionListener {
    suspend fun onNodeStatusChanged(node: Node, status: NodeStatus, throwable: Throwable?)
}