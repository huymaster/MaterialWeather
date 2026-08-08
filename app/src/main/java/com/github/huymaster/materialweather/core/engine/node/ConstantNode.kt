package com.github.huymaster.materialweather.core.engine.node

import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.NodeBundle
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData

@Suppress("UNCHECKED_CAST")
class ConstantNode(data: RestoreData = RestoreData.EMPTY) : Node(data) {
    constructor(value: Number) : this() {
        setValue(value)
    }

    override val name: Int = R.string.node_constant
    private var outputParam: NodeParam.Output<Number> =
        NodeParam.output("value", data.getParamId("value"))

    var value: Number = 0
        private set

    init {
        restore(data)
    }

    override fun getInputs(): Set<NodeParam.Input<*>> = emptySet()
    override fun getOutputs(): Set<NodeParam.Output<*>> = setOf(outputParam)

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        context.set(outputParam, value)
    }

    override fun serialize(data: NodeBundle) {
        val type = value::class.java.name
        data.putString(TYPE_KEY, type)
        data.put(VALUE_KEY, value)
    }

    fun setValue(value: Number) {
        this.value = value
    }

    private fun restore(data: RestoreData) {
        val type = data.getString(TYPE_KEY) ?: return
        val valueClass = Class.forName(type)
        val value = data.get(VALUE_KEY, valueClass.kotlin) ?: return
        if (value is Number) this.value = value
    }

    private companion object {
        const val TYPE_KEY = "type"
        const val VALUE_KEY = "value"
    }
}