package com.github.huymaster.materialweather.core.engine

import androidx.annotation.StringRes
import com.github.huymaster.materialweather.core.engine.serialization.NodeBundle
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData
import kotlin.uuid.Uuid

abstract class Node(protected val data: RestoreData = RestoreData.EMPTY) {
    val id: String = Uuid.random().toString()

    @get:StringRes
    abstract val name: Int

    abstract fun getInputs(): Set<NodeParam.Input<*>>
    abstract fun getOutputs(): Set<NodeParam.Output<*>>

    fun accept(visitor: NodeVisitor) = visitor.visit(this)
    protected fun skip(): Nothing = throw NodeException.FlowSkipped(id)

    abstract suspend fun execute(context: NodeExecutionEngine.ExecutionContext)
    open fun serialize(data: NodeBundle) {
        // Nothing to do by default
    }
}