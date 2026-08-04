package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import kotlin.reflect.KClass

object NodeRegistry {
    private val loader: ClassLoader =
        Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
    private val factories = mutableMapOf<String, NodeFactory<*>>()

    fun register(vararg facs: NodeFactory<*>) {
        facs.forEach { factory ->
            val supportedTypes = factory.types.map { it.java.name }.toSet()
            if (factories.keys.any { it in supportedTypes })
                throw NodeException.NodeFactoryRegisteredTwice(
                    factories.keys.intersect(supportedTypes)
                )
            supportedTypes.forEach { factories[it] = factory }
        }
    }

    fun getFactory(type: String): NodeFactory<*>? {
        val clazz: Class<*> =
            loader.loadClass(type) ?: throw NodeException.InvalidType(Node::class, Unit::class)
        if (!Node::class.java.isAssignableFrom(clazz))
            throw NodeException.InvalidType(Node::class, clazz.kotlin)

        return factories[clazz.name]
    }

    fun <N : Node> getFactory(type: KClass<N>): NodeFactory<N>? {
        return factories.values
            .filter { type in it.types }
            .filterIsInstance<NodeFactory<N>>()
            .firstOrNull()
    }

    fun create(dto: NodeDto): Node {
        val factory = getFactory(dto.type)
            ?: throw NodeException.NodeFactoryNotFound(dto.type)

        val paramIdMap = mutableMapOf<String, String>()
        dto.inputs.forEach { paramIdMap[it.name] = it.id }
        dto.outputs.forEach { paramIdMap[it.name] = it.id }
        return factory.create(SerializationData(paramIdMap, dto.customData))
    }

    fun <N : Node> createWith(dto: NodeDto, factory: NodeFactory<N>): N {
        if (dto.type !in factory.types.map { it.java.name })
            throw NodeException.InvalidFactory(dto.type)

        val paramIdMap = mutableMapOf<String, String>()
        dto.inputs.forEach { paramIdMap[it.name] = it.id }
        dto.outputs.forEach { paramIdMap[it.name] = it.id }
        return factory.create(SerializationData(paramIdMap, dto.customData))
    }

    fun <N : Node> createWith(dto: NodeDto, type: KClass<N>): N {
        val factory = getFactory(type)
            ?: throw NodeException.NodeFactoryNotFound(type.java.name)

        val paramIdMap = mutableMapOf<String, String>()
        dto.inputs.forEach { paramIdMap[it.name] = it.id }
        dto.outputs.forEach { paramIdMap[it.name] = it.id }
        return factory.create(SerializationData(paramIdMap, dto.customData))
    }
}