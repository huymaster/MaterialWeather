package com.github.huymaster.materialweather.core.engine.serialization

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
)