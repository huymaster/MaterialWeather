package com.github.huymaster.materialweather.core.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class NodeExecutionEngine(
    context: CoroutineContext = EmptyCoroutineContext,
    private val executionStrategy: ExecutionStrategy = ParallelExecutionStrategy(),
    private val listener: NodeExecutionListener? = null
) {
    private val scope = CoroutineScope(context + SupervisorJob())

    fun execute(graph: NodeGraph): Job = scope.launch {
        val executionContext = DefaultExecutionContext()
        executionStrategy.execute(graph, executionContext, listener)
    }

    interface ExecutionContext {
        suspend fun <T : Any> get(param: NodeParam.Input<T>): T?
        suspend fun <T : Any> set(param: NodeParam.Output<T>, value: T?)
        suspend fun transfer(fromParamId: String, toParamId: String)
    }

    private class DefaultExecutionContext : ExecutionContext {
        private val values = mutableMapOf<String, NullSafeValue<*>>()
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
    }
}