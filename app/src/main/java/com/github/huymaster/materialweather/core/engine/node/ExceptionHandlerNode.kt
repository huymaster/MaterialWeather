package com.github.huymaster.materialweather.core.engine.node

import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.SerializationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ExceptionHandlerNode(data: SerializationData = SerializationData.EMPTY) : Node(data) {
    override val name: Int = 0

    private val exceptionInput =
        NodeParam.input<NodeException>(EXCEPTION_KEY, id = data.getParamId(EXCEPTION_KEY))

    private val serializationData = SerializationData(
        mapOf(EXCEPTION_KEY to exceptionInput.id)
    )

    private val _message = MutableSharedFlow<String>(0)
    val message: SharedFlow<String> get() = _message.asSharedFlow()

    override fun getInputs(): Set<NodeParam.Input<*>> = setOf(exceptionInput)
    override fun getOutputs(): Set<NodeParam.Output<*>> = setOf()

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        val exception = context.get(exceptionInput) ?: skip()
        val resource = context.androidContext
        val message = when (exception) {
            is NodeException.DuplicateConnection ->
                resource.getString(R.string.exception_duplicate_connection, exception.connectionId)

            is NodeException.InfinityLoop ->
                resource.getString(R.string.exception_infinity_loop)

            is NodeException.InvalidType ->
                resource.getString(
                    R.string.exception_invalid_type,
                    exception.expected,
                    exception.actual
                )

            is NodeException.MissingRequiredInput ->
                TODO()

            is NodeException.ParamNotFound ->
                resource.getString(R.string.exception_null_param, exception.paramId)

            else -> skip()
        }
        _message.emit(message)
    }

    override fun getSerializationData(): SerializationData = serializationData

    companion object {
        private const val EXCEPTION_KEY = "exception"
    }
}