package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import kotlin.reflect.KClass

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