package com.github.huymaster.materialweather.core.engine.node

import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.NodeBundle
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
class ConstantNode(data: RestoreData = RestoreData.EMPTY) : Node(data) {
    override val name: Int = R.string.node_constant
    private var value: @Serializable Any = ""
    private var outputParam: NodeParam.Output<*> =
        NodeParam.Output("value", value::class, data.getParamId("value"))

    override fun getInputs(): Set<NodeParam.Input<*>> = emptySet()
    override fun getOutputs(): Set<NodeParam.Output<*>> = setOf(outputParam)

    init {
        restoreValue()
    }

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        context.set(outputParam as NodeParam.Output<Any>, value)
    }

    override fun serialize(data: NodeBundle) {
        val type = value::class.java.name
        data.putString(TYPE_KEY, type)
        data.put(VALUE_KEY, value)
    }

    fun setValue(value: @Serializable Any) {
        this.value = value
        updateParam()
    }

    private fun updateParam() {
        val id = outputParam.id
        outputParam = NodeParam.Output("value", value::class, id)
    }

    private fun restoreValue() {
        val type = data.getString(TYPE_KEY) ?: return
        val valueClass = Class.forName(type)
        val value = data.get(VALUE_KEY, valueClass.kotlin) ?: return
        this.value = value
        updateParam()
    }

    private companion object {
        const val TYPE_KEY = "type"
        const val VALUE_KEY = "value"
    }
}