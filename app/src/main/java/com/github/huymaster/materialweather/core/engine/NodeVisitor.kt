package com.github.huymaster.materialweather.core.engine

fun interface NodeVisitor {
    fun visit(node: Node)
}