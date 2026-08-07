package com.github.huymaster.materialweather.core.engine

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.github.huymaster.materialweather.core.engine.serialization.NodeGraphDto

class NodeGraph(
    private val sorter: NodeSorter = NodeSorter()
) {
    private val _nodes = mutableMapOf<String, Node>()
    private val _connections = mutableSetOf<ParamConnection>()

    private val _adjacencyMap = mutableMapOf<String, MutableSet<String>>()
    private val paramMap = mutableMapOf<String, NodeParam<*>>()

    val nodes: Set<Node> get() = _nodes.values.toSet()
    val connections: Set<ParamConnection> get() = _connections.toSet()

    fun addNode(node: Node) {
        _nodes[node.id] = node

        val inputs = node.getInputs()
        val outputs = node.getOutputs()
        inputs.forEach { paramMap[it.id] = it }
        outputs.forEach { paramMap[it.id] = it }
    }

    fun removeNode(nodeId: String) {
        val node = _nodes.remove(nodeId) ?: return
        val inputs = node.getInputs()
        val outputs = node.getOutputs()

        val nodeParamIds = (inputs.map { it.id } + outputs.map { it.id }).toSet()

        _connections.removeIf { it.fromParamId in nodeParamIds || it.toParamId in nodeParamIds }
        nodeParamIds.forEach { id ->
            paramMap.remove(id)
            _adjacencyMap.remove(id)
        }
        _adjacencyMap.values.forEach { targets -> targets.removeAll(nodeParamIds) }
    }

    fun connect(from: NodeParam.Output<*>, to: NodeParam.Input<*>) {
        if (!paramMap.containsKey(from.id))
            throw NodeException.ParamNotFound(from.id)
        if (!paramMap.containsKey(to.id))
            throw NodeException.ParamNotFound(to.id)
        ConnectionValidator.validate(from, to, _connections)

        val connection = ParamConnection(fromParamId = from.id, toParamId = to.id)
        if (_connections.add(connection))
            _adjacencyMap.getOrPut(from.id) { mutableSetOf() }.add(to.id)
    }

    fun disconnect(from: NodeParam.Output<*>, to: NodeParam.Input<*>) {
        val connection = ParamConnection(fromParamId = from.id, toParamId = to.id)
        if (_connections.remove(connection)) {
            _adjacencyMap[from.id]?.remove(to.id)
        }
    }

    fun getTargetParams(fromParamId: String): List<NodeParam.Input<*>> {
        return _adjacencyMap[fromParamId]
            ?.mapNotNull { paramMap[it] as? NodeParam.Input<*> }
            .orEmpty()
    }

    fun getExecutionOrder(): List<Node> {
        return sorter.sort(_nodes.values, _connections)
    }

    fun getExecutionLayers(): List<List<Node>> {
        return sorter.sortInLayers(_nodes.values, _connections)
    }
}

object NodeGraphSaver : Saver<NodeGraph, NodeGraphDto> {
    override fun SaverScope.save(value: NodeGraph): NodeGraphDto {
        return NodeGraphDto.fromNodeGraph(value)
    }

    override fun restore(value: NodeGraphDto): NodeGraph {
        return value.toNodeGraph()
    }

}