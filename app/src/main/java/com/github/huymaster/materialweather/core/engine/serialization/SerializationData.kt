package com.github.huymaster.materialweather.core.engine.serialization

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SerializationData(
    val paramIdMap: Map<String, String>,
    val customData: Map<String, String> = emptyMap()
) {
    companion object {
        val EMPTY = SerializationData(emptyMap(), emptyMap())
    }

    fun getParamId(name: String): String = paramIdMap[name] ?: Uuid.random().toString()
}