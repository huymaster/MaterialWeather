package com.github.huymaster.materialweather.core.engine

import kotlin.uuid.Uuid

abstract class Node {
    val id: String = Uuid.random().toString()

    abstract val inputs: Set<NodeParam.Input<*>>
    abstract val outputs: Set<NodeParam.Output<*>>

    fun accept(visitor: NodeVisitor) = visitor.visit(this)
    abstract suspend fun execute(context: NodeExecutionEngine.ExecutionContext)
}