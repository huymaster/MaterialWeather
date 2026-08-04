package com.github.huymaster.materialweather.core.engine

import android.content.Context
import com.github.huymaster.materialweather.core.engine.node.ExceptionHandlerNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class NodeExecutionEngine(
    context: CoroutineContext = EmptyCoroutineContext,
    private val executionStrategy: ExecutionStrategy = ParallelExecutionStrategy(),
    private val listener: NodeExecutionListener? = null
) : KoinComponent {
    private val scope = CoroutineScope(context + SupervisorJob())

    fun execute(graph: NodeGraph): Job = scope.launch {
        val executionContext = DefaultExecutionContext(get())
        try {
            executionStrategy.execute(graph, executionContext, listener)
        } catch (e: NodeException) {
            val handlers = graph.nodes.filterIsInstance<ExceptionHandlerNode>()
            if (handlers.isNotEmpty()) {
                val tmp = NodeParam.output<NodeException>("tmp")
                executionContext.set(tmp, e)
                for (handler in handlers) {
                    executionContext.transfer(tmp.id, handler.getInputs().first().id)
                    runCatching { handler.execute(executionContext) }
                        .onSuccess {
                            listener?.onNodeStatusChanged(
                                handler,
                                NodeStatus.COMPLETED,
                                null
                            )
                        }
                        .onFailure {
                            listener?.onNodeStatusChanged(
                                handler,
                                NodeStatus.FAILED,
                                it
                            )
                        }
                }
            }
        }
    }

    interface ExecutionContext {
        val androidContext: Context

        suspend fun <T : Any> get(param: NodeParam.Input<T>): T?
        suspend fun <T : Any> set(param: NodeParam.Output<T>, value: T?)
        suspend fun transfer(fromParamId: String, toParamId: String)

        suspend fun markSkipped(nodeId: String)
        suspend fun isSkipped(nodeId: String): Boolean
    }

    private class DefaultExecutionContext(override val androidContext: Context) : ExecutionContext {
        private val values = mutableMapOf<String, NullSafeValue<*>>()
        private val skippedNodes = mutableSetOf<String>()
        private val mutex = Mutex()

        @Suppress("UNCHECKED_CAST")
        override suspend fun <T : Any> get(param: NodeParam.Input<T>): T? = mutex.withLock {
            val nullSafe = values[param.id] ?: NullSafeValue.of(param.defaultValue)
            when (nullSafe) {
                is NullSafeValue.NullValue -> null
                is NullSafeValue.Value<*> -> nullSafe.value as? T
            }
        }

        override suspend fun <T : Any> set(param: NodeParam.Output<T>, value: T?) =
            mutex.withLock { values[param.id] = NullSafeValue.of(value) }

        override suspend fun transfer(fromParamId: String, toParamId: String) {
            mutex.withLock { values[fromParamId]?.let { values[toParamId] = it } }
        }

        override suspend fun markSkipped(nodeId: String) {
            mutex.withLock { skippedNodes.add(nodeId) }
        }

        override suspend fun isSkipped(nodeId: String): Boolean = mutex.withLock {
            skippedNodes.contains(nodeId)
        }
    }
}