package com.github.huymaster.materialweather.core.engine.node

import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ExceptionHandlerNode(data: RestoreData = RestoreData.EMPTY) : Node() {
    override val name: Int = 0

    private val _message = MutableSharedFlow<String>(0)
    val message: SharedFlow<String> get() = _message.asSharedFlow()

    override fun getInputs(): Set<NodeParam.Input<*>> = setOf()
    override fun getOutputs(): Set<NodeParam.Output<*>> = setOf()

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        TODO()
    }

    companion object {
        private const val EXCEPTION_KEY = "exception"
    }
}