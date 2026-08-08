package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

interface NodeFactory<N : Node> {
    val type: KClass<N>
    fun create(data: RestoreData): N

    companion object {
        fun <N : Node> simple(
            type: KClass<N>,
            creator: (RestoreData) -> N
        ): NodeFactory<N> = SimpleNodeFactory(type, creator)
    }
}

private class SimpleNodeFactory<N : Node>(
    override val type: KClass<N>,
    private val creator: (RestoreData) -> N
) : NodeFactory<N> {
    override fun create(data: RestoreData): N = creator(data)
}

class DefaultFactory<T : Node>(
    override val type: KClass<T>
) : NodeFactory<T> {
    private val targetConstructor: KFunction<T>? = findValidConstructor()

    override fun create(data: RestoreData): T {
        val constructor = targetConstructor
            ?: throw NodeException.CannotDeserialize("No suitable constructor found for ${type.simpleName}")

        return runCatching {
            val params = constructor.valueParameters
            if (params.isEmpty())
                constructor.call()
            else
                constructor.call(data)
        }.getOrElse { cause ->
            throw NodeException.CannotDeserialize("Failed to instantiate ${type.simpleName}: ${cause.message}")
        }
    }

    private fun findValidConstructor(): KFunction<T>? {
        type.primaryConstructor?.takeIf { isConstructorValid(it) }?.let { return it }
        return type.constructors.firstOrNull { isConstructorValid(it) }
    }

    private fun isConstructorValid(constructor: KFunction<T>): Boolean {
        val params = constructor.valueParameters
        return when (params.size) {
            1 -> params.first().type.classifier == RestoreData::class
            0 -> true
            else -> false
        }
    }
}