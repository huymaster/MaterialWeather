package com.github.huymaster.materialweather.core.engine

import androidx.annotation.StringRes
import kotlin.reflect.KClass

sealed class NodeException(
    override val cause: Throwable? = null
) : RuntimeException(cause) {
    class InfinityLoop : NodeException()
    class InvalidType(val expected: KClass<*>, val actual: KClass<*>) : NodeException()
    class InvalidFactory(val node: String) : NodeException()
    class ParamNotFound(val paramId: String) : NodeException()
    class DuplicateConnection(val connectionId: String) : NodeException()
    class MissingRequiredInput(val nodeId: String, val paramId: String) : NodeException()
    class FlowSkipped(val nodeId: String) : NodeException()
    class NodeFactoryRegisteredTwice(val types: Set<String>) : NodeException()
    class NodeFactoryNotFound(val type: String) : NodeException()
    class CannotSerializeNode(val nodeId: String) : NodeException()
    class CannotDeserialize(@StringRes val msg: Int) : NodeException()
    class CannotGetLocation(@StringRes val msg: Int) : NodeException()
}