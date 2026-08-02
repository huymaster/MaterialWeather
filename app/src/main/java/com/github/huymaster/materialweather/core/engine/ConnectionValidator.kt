package com.github.huymaster.materialweather.core.engine

import kotlin.reflect.full.isSubclassOf

object ConnectionValidator {
    fun validate(
        from: NodeParam.Output<*>,
        to: NodeParam.Input<*>,
        connections: Set<ParamConnection>
    ) {
        if (!from.type.isSubclassOf(to.type))
            throw NodeException.InvalidType(expected = to.type, actual = from.type)

        val isInputAlreadyConnected = connections.any { it.toParamId == to.id }
        if (isInputAlreadyConnected)
            throw NodeException.DuplicateConnection(to.id)
    }
}