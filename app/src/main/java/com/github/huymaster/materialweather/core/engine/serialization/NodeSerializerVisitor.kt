package com.github.huymaster.materialweather.core.engine.serialization

import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeVisitor

class NodeSerializerVisitor : NodeVisitor {
    var resultDto: NodeDto? = null
        private set

    override fun visit(node: Node) {
        val inputsDto = node.getInputs().map { ParamMappingDto(name = it.name, id = it.id) }
        val outputsDto = node.getOutputs().map { ParamMappingDto(name = it.name, id = it.id) }
        val bundle = NodeBundle()
        node.serialize(bundle)

        resultDto = NodeDto(
            id = node.id,
            type = node::class.java.name,
            params = inputsDto + outputsDto,
            data = bundle.toMap()
        )
    }
}