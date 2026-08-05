package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException

object NodeRegistry {
    private val registry = mutableMapOf<String, NodeFactory<*>>()

    fun register(vararg factories: NodeFactory<*>) {
        for (factory in factories) {
            validate(factory)
            val keys = getKeys(factory)
            for (key in keys)
                if (key !in registry.keys) registry[key] = factory
        }
    }

    inline fun <reified N : Node> register(noinline creator: (RestoreData) -> N) {
        val factory = NodeFactory.simple(N::class, creator)
        register(factory)
    }

    fun create(dto: NodeDto): Node {
        val factory = registry[dto.type]
            ?: throw NodeException.NodeFactoryNotFound(dto.type)

        val paramIdMap = buildMap {
            dto.params.forEach { put(it.name, it.id) }
        }
        val restoreData = RestoreData(paramIdMap, NodeBundle.fromMap(dto.data))
        return factory.create(restoreData)
    }

    private fun getKeys(factory: NodeFactory<*>): Set<String> {
        val keys = listOfNotNull(factory.type.qualifiedName, factory.type.java.name)
            .toSet()
        return keys
    }

    private fun validate(factory: NodeFactory<*>): Nothing? {
        val keys = getKeys(factory)
        if (keys.all { registry.keys.contains(it) })
            throw NodeException.NodeFactoryRegisteredTwice(keys)
        return null
    }
}