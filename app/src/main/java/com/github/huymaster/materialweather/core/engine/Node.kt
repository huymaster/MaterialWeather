package com.github.huymaster.materialweather.core.engine

import androidx.annotation.StringRes
import com.github.huymaster.materialweather.core.engine.serialization.NodeBundle
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData
import kotlin.uuid.Uuid

abstract class Node(protected val data: RestoreData = RestoreData.EMPTY) {
    var id: String = Uuid.random().toString()
        private set

    @get:StringRes
    abstract val name: Int

    abstract fun getInputs(): Set<NodeParam.Input<*>>
    abstract fun getOutputs(): Set<NodeParam.Output<*>>

    fun accept(visitor: NodeVisitor) = visitor.visit(this)
    protected fun skip(): Nothing = throw NodeException.FlowSkipped(id)

    abstract suspend fun execute(context: NodeExecutionEngine.ExecutionContext)

    fun changeId(id: String) {
        this.id = id
    }

    open fun serialize(data: NodeBundle) {
        // Nothing to do by default
    }

    override fun toString(): String {
        val type = javaClass.kotlin.simpleName ?: javaClass.simpleName
        return "Node[$type](id='$id', name=${name})"
    }
}