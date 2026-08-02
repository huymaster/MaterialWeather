package com.github.huymaster.materialweather.core.engine

import kotlin.reflect.KClass

sealed class NodeException(
    override val cause: Throwable? = null
) : RuntimeException(cause) {
    class InfinityLoop : NodeException()
    class InvalidType(val expected: KClass<*>, val actual: KClass<*>) : NodeException()
    class ParamNotFound(val paramId: String) : NodeException()
    class DuplicateConnection(val connectionId: String) : NodeException()
    class MissingRequiredInput(val nodeId: String, val paramId: String) : NodeException()
}