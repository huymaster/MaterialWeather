package com.github.huymaster.materialweather.core.engine

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

abstract class ExecutionStrategy {
    abstract suspend fun execute(
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    )

    protected suspend fun executeSingleNode(
        node: Node,
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    ) {
        try {
            var shouldSkip = false
            for (input in node.getInputs()) {
                val value = context.get(input)
                if (value == null && !input.isOptional) {
                    shouldSkip = true
                    break
                }
            }

            if (shouldSkip) {
                context.markSkipped(node.id)
                listener?.onNodeStatusChanged(node, NodeStatus.SKIPPED, null)
                return
            }

            listener?.onNodeStatusChanged(node, NodeStatus.RUNNING, null)

            node.execute(context)

            for (output in node.getOutputs()) {
                val targetInputs = graph.getTargetParams(output.id)
                for (targetInput in targetInputs) {
                    context.transfer(fromParamId = output.id, toParamId = targetInput.id)
                }
            }

            listener?.onNodeStatusChanged(node, NodeStatus.COMPLETED, null)

        } catch (e: NodeException.FlowSkipped) {
            context.markSkipped(node.id)
            listener?.onNodeStatusChanged(node, NodeStatus.SKIPPED, e)
        } catch (e: Throwable) {
            listener?.onNodeStatusChanged(node, NodeStatus.FAILED, e)
            throw e
        }
    }
}

class ParallelExecutionStrategy : ExecutionStrategy() {
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
}

class SequentialExecutionStrategy : ExecutionStrategy() {
    override suspend fun execute(
        graph: NodeGraph,
        context: NodeExecutionEngine.ExecutionContext,
        listener: NodeExecutionListener?
    ) {
        val nodes = graph.getExecutionOrder()

        for (node in nodes) {
            executeSingleNode(node, graph, context, listener)
        }
    }
}