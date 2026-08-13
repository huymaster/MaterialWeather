package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.node.ConstantNode
import com.github.huymaster.materialweather.core.engine.node.ExceptionHandlerNode
import com.github.huymaster.materialweather.core.engine.node.LocationFetcherNode
import kotlin.reflect.KClass

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
        val paramIdMap = buildMap {
            dto.params.forEach { put(it.name, it.id) }
        }
        val restoreData = RestoreData(paramIdMap, NodeBundle.fromMap(dto.data))

        val factory = registry[dto.type]
        val node = create(factory, dto.type, restoreData)
        node.changeId(dto.id)
        return node
    }

    @Suppress("UNCHECKED_CAST")
    private fun create(
        factory: NodeFactory<*>?,
        type: String,
        data: RestoreData
    ): Node {
        if (factory != null) return factory.create(data)
        try {
            val clazz =
                Class.forName(type).kotlin as? KClass<Node>
                    ?: throw NodeException.InvalidFactory(type)
            val default = DefaultFactory(clazz)
            return default.create(data)
        } catch (_: Exception) {
            throw NodeException.NodeFactoryNotFound(data.getString("type") ?: "unknown")
        }
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

    init {
        register(::ConstantNode)
        register(::LocationFetcherNode)
        register(::ExceptionHandlerNode)
    }
}