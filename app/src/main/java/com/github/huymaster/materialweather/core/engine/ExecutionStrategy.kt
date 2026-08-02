package com.github.huymaster.materialweather.core.engine

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

fun interface ExecutionStrategy {
    suspend fun execute(
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    )
}

class ParallelExecutionStrategy : ExecutionStrategy {
    override suspend fun execute(
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    ) = coroutineScope {
        val layers = graph.getExecutionLayers()

        for (layer in layers) {
            ensureActive()
            layer.map { node ->
                async { executeSingleNode(node, graph, context, listener) }
            }.awaitAll()
        }
    }

    private suspend fun executeSingleNode(
        node: Node,
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    ) {
        try {
            listener?.onNodeStatusChanged(node, NodeStatus.RUNNING, null)

            for (input in node.inputs) {
                val value = context.get(input)
                if (value == null && !input.isOptional) {
                    throw NodeException.MissingRequiredInput(node.id, input.id)
                }
            }

            node.execute(context)

            for (output in node.outputs) {
                val targetInputs = graph.getTargetParams(output.id)
                for (targetInput in targetInputs) {
                    context.transfer(fromParamId = output.id, toParamId = targetInput.id)
                }
            }

            listener?.onNodeStatusChanged(node, NodeStatus.COMPLETED, null)
        } catch (e: Throwable) {
            listener?.onNodeStatusChanged(node, NodeStatus.FAILED, e)
            throw e
        }
    }
}

class SequentialExecutionStrategy : ExecutionStrategy {
    override suspend fun execute(
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    ) {
        val nodes = graph.getExecutionOrder()

        for (node in nodes) {
            try {
                listener?.onNodeStatusChanged(node, NodeStatus.RUNNING, null)

                for (input in node.inputs) {
                    val value = context.get(input)
                    if (value == null && !input.isOptional) {
                        throw NodeException.MissingRequiredInput(node.id, input.id)
                    }
                }

                node.execute(context)

                for (output in node.outputs) {
                    val targetInputs = graph.getTargetParams(output.id)
                    for (targetInput in targetInputs) {
                        context.transfer(fromParamId = output.id, toParamId = targetInput.id)
                    }
                }

                listener?.onNodeStatusChanged(node, NodeStatus.COMPLETED, null)
            } catch (e: Throwable) {
                listener?.onNodeStatusChanged(node, NodeStatus.FAILED, e)
                throw e
            }
        }
    }
}