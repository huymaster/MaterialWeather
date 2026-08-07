package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.NodeGraph
import com.github.huymaster.materialweather.core.engine.NodeParam
import kotlinx.serialization.json.Json

interface NodeGraphSerializer {
    fun serialize(graph: NodeGraph): String
    fun deserialize(jsonString: String): NodeGraph

    companion object : NodeGraphSerializer by NodeGraphSerializerImpl()
}

private class NodeGraphSerializerImpl(json: Json = Json.Default) : NodeGraphSerializer {
    private val JSON = Json(json) { ignoreUnknownKeys = true }
    private val registry: NodeRegistry = NodeRegistry

    fun toDto(graph: NodeGraph): NodeGraphDto {
        val nodeDtos = graph.nodes.map { node ->
            val visitor = NodeSerializerVisitor()
            node.accept(visitor)
            visitor.resultDto ?: throw NodeException.CannotSerializeNode(node.id)
        }

        val connectionDtos = graph.connections.map { connection ->
            ParamConnectionDto(
                fromParamId = connection.fromParamId,
                toParamId = connection.toParamId
            )
        }

        return NodeGraphDto(nodes = nodeDtos, connections = connectionDtos)
    }

    override fun serialize(graph: NodeGraph): String {
        val dto = toDto(graph)
        return JSON.encodeToString(dto)
    }

    fun fromDto(dto: NodeGraphDto): NodeGraph {
        val graph = NodeGraph()
        val paramMap = mutableMapOf<String, NodeParam<*>>()

        for (nodeDto in dto.nodes) {
            val node = registry.create(nodeDto)
            graph.addNode(node)

            node.getInputs().forEach { paramMap[it.id] = it }
            node.getOutputs().forEach { paramMap[it.id] = it }
        }

        for (connDto in dto.connections) {
            val fromParam = paramMap[connDto.fromParamId] as? NodeParam.Output<*>
            val toParam = paramMap[connDto.toParamId] as? NodeParam.Input<*>

            if (fromParam != null && toParam != null) {
                graph.connect(fromParam, toParam)
            }
        }

        return graph
    }

    override fun deserialize(jsonString: String): NodeGraph {
        val dto = JSON.decodeFromString<NodeGraphDto>(jsonString)
        return fromDto(dto)
    }
}