package com.github.huymaster.materialweather.core.engine

import androidx.annotation.StringRes
import com.github.huymaster.materialweather.core.engine.serialization.SerializableNode
import com.github.huymaster.materialweather.core.engine.serialization.SerializationData
import kotlin.uuid.Uuid

abstract class Node(protected val data: SerializationData = SerializationData.EMPTY) :
    SerializableNode {
    val id: String = Uuid.random().toString()

    @get:StringRes
    abstract val name: Int

    abstract fun getInputs(): Set<NodeParam.Input<*>>
    abstract fun getOutputs(): Set<NodeParam.Output<*>>

    fun accept(visitor: NodeVisitor) = visitor.visit(this)
    protected fun skip(): Nothing = throw NodeException.FlowSkipped(id)

    abstract suspend fun execute(context: NodeExecutionEngine.ExecutionContext)
}