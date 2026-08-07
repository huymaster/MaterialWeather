package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.NodeGraph
import kotlinx.serialization.json.Json

interface NodeGraphSerializer {
    fun serialize(graph: NodeGraph): String
    fun deserialize(jsonString: String): NodeGraph

    companion object : NodeGraphSerializer by NodeGraphSerializerImpl()
}

private class NodeGraphSerializerImpl(json: Json = Json.Default) : NodeGraphSerializer {
    private val JSON = Json(json) { ignoreUnknownKeys = true }

    override fun serialize(graph: NodeGraph): String {
        val dto = NodeGraphDto.fromNodeGraph(graph)
        return JSON.encodeToString(dto)
    }

    override fun deserialize(jsonString: String): NodeGraph {
        val dto = JSON.decodeFromString<NodeGraphDto>(jsonString)
        return dto.toNodeGraph()
    }
}