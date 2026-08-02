package com.github.huymaster.materialweather.core.engine

class NodeSorter {
    fun sort(
        nodes: Collection<Node>,
        connections: Set<ParamConnection>
    ): List<Node> {
        return sortInLayers(nodes, connections).flatten()
    }

    fun sortInLayers(
        nodes: Collection<Node>,
        connections: Set<ParamConnection>
    ): List<List<Node>> {
        val paramToNode = mutableMapOf<String, Node>()
        for (node in nodes) {
            for (input in node.inputs) paramToNode[input.id] = node
            for (output in node.outputs) paramToNode[output.id] = node
        }

        val inDegree = mutableMapOf<String, Int>()
        val adjacencyList = mutableMapOf<String, MutableSet<String>>()

        for (node in nodes) {
            inDegree[node.id] = 0
            adjacencyList[node.id] = mutableSetOf()
        }

        for (connection in connections) {
            val sourceNode = paramToNode[connection.fromParamId]
            val targetNode = paramToNode[connection.toParamId]

            if (sourceNode != null && targetNode != null && sourceNode.id != targetNode.id) {
                if (adjacencyList[sourceNode.id]?.add(targetNode.id) == true) {
                    inDegree[targetNode.id] = (inDegree[targetNode.id] ?: 0) + 1
                }
            }
        }

        val nodeMap = nodes.associateBy { it.id }
        var currentLayer = nodes.filter { (inDegree[it.id] ?: 0) == 0 }
        val layers = mutableListOf<List<Node>>()
        var processedCount = 0

        while (currentLayer.isNotEmpty()) {
            layers.add(currentLayer)
            processedCount += currentLayer.size

            val nextLayer = mutableListOf<Node>()
            for (node in currentLayer) {
                val neighbors = adjacencyList[node.id].orEmpty()
                for (neighborId in neighbors) {
                    val updatedInDegree = (inDegree[neighborId] ?: 0) - 1
                    inDegree[neighborId] = updatedInDegree
                    if (updatedInDegree == 0) {
                        nodeMap[neighborId]?.let { nextLayer.add(it) }
                    }
                }
            }
            currentLayer = nextLayer
        }

        if (processedCount != nodes.size) {
            throw NodeException.InfinityLoop()
        }

        return layers
    }
}