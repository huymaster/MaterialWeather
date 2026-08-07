package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.NodeGraph
import com.github.huymaster.materialweather.core.engine.NodeParam
import kotlinx.serialization.Serializable

@Serializable
data class ParamMappingDto(
    val name: String,
    val id: String
)

@Serializable
data class NodeDto(
    val id: String,
    val type: String,
    val params: List<ParamMappingDto> = emptyList(),
    val data: Map<String, String> = emptyMap()
)

@Serializable
data class ParamConnectionDto(
    val fromParamId: String,
    val toParamId: String
)

@Serializable
data class NodeGraphDto(
    val nodes: List<NodeDto>,
    val connections: List<ParamConnectionDto>
) {
    fun toNodeGraph(): NodeGraph {
        val graph = NodeGraph()
        val paramMap = mutableMapOf<String, NodeParam<*>>()

        for (nodeDto in nodes) {
            val node = NodeRegistry.create(nodeDto)
            graph.addNode(node)

            node.getInputs().forEach { paramMap[it.id] = it }
            node.getOutputs().forEach { paramMap[it.id] = it }
        }

        for (connDto in connections) {
            val fromParam = paramMap[connDto.fromParamId] as? NodeParam.Output<*>
            val toParam = paramMap[connDto.toParamId] as? NodeParam.Input<*>

            if (fromParam != null && toParam != null) {
                graph.connect(fromParam, toParam)
            }
        }

        return graph
    }

    companion object {
        fun fromNodeGraph(graph: NodeGraph): NodeGraphDto {
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
    }
}