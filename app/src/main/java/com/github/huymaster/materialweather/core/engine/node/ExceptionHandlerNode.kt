package com.github.huymaster.materialweather.core.engine.node

import android.content.res.Resources
import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ExceptionHandlerNode(data: RestoreData = RestoreData.EMPTY) : Node(data) {
    override val name: Int = R.string.node_exception_handler

    private val exception =
        NodeParam.input<NodeException>(EXCEPTION_KEY, id = data.getParamId(EXCEPTION_KEY))
    private val _message = MutableSharedFlow<String>(0)

    val message: SharedFlow<String> get() = _message.asSharedFlow()

    override fun getInputs(): Set<NodeParam.Input<*>> = setOf(exception)
    override fun getOutputs(): Set<NodeParam.Output<*>> = emptySet()

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        val exception = context.get<NodeException>(this.exception) ?: skip()
        val res: Resources = context.androidContext.resources
        val message = when (exception) {
            is NodeException.CannotDeserialize -> {
                val cause = res.getString(exception.msg, *exception.args)
                res.getString(R.string.exception_cannot_deserialize, cause)
            }

            is NodeException.CannotSerializeNode ->
                res.getString(R.string.exception_cannot_serialize_node, exception.nodeId)

            is NodeException.DuplicateConnection ->
                res.getString(R.string.exception_duplicate_connection, exception.connectionId)

            is NodeException.FlowSkipped ->
                res.getString(R.string.exception_flow_skipped, exception.nodeId)

            is NodeException.InfinityLoop ->
                res.getString(R.string.exception_infinity_loop)

            is NodeException.InvalidFactory ->
                res.getString(R.string.exception_invalid_factory, exception.node)

            is NodeException.InvalidType ->
                res.getString(
                    R.string.exception_invalid_type,
                    exception.expected.java.name,
                    exception.actual.java.name
                )

            is NodeException.MissingRequiredInput ->
                res.getString(R.string.exception_missing_input, exception.nodeId, exception.paramId)

            is NodeException.NodeFactoryNotFound ->
                res.getString(R.string.exception_node_factory_not_found, exception.type)

            is NodeException.NodeFactoryRegisteredTwice ->
                res.getString(R.string.exception_node_factory_registered_twice, exception.types)

            is NodeException.ParamNotFound ->
                res.getString(R.string.exception_null_param, exception.paramId)

            is NodeException.CannotGetLocation -> {
                val cause = res.getString(exception.msg)
                res.getString(R.string.exception_cannot_get_location, cause)
            }

            is NodeException.DivideByZero ->
                res.getString(R.string.exception_divide_by_zero)
        }
        _message.emit(message)
        skip()
    }

    companion object {
        private const val EXCEPTION_KEY = "exception"
    }
}