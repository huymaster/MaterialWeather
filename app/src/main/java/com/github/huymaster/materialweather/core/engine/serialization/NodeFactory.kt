package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import kotlin.reflect.KClass

interface NodeFactory<N : Node> {
    val types: Set<KClass<N>>

    fun create(data: SerializationData): N
}