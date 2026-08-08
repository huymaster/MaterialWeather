package com.github.huymaster.materialweather.core.engine.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.uuid.Uuid

@Serializable
data class NodeBundle(
    private val data: MutableMap<String, String> = mutableMapOf()
) {
    @Suppress("UNCHECKED_CAST")
    @OptIn(InternalSerializationApi::class)
    fun <T : Any> put(
        key: String,
        value: @Serializable T,
        serializer: SerializationStrategy<T> = value::class.serializer() as SerializationStrategy<T>
    ) {
        data[key] = Json.encodeToString(serializer, value)
    }

    fun putString(key: String, value: String) {
        data[key] = value
    }

    fun <T> get(
        key: String,
        deserializer: DeserializationStrategy<T>
    ): T? {
        val value = data[key] ?: return null
        return Json.decodeFromString(deserializer, value)
    }

    fun getString(key: String): String? = data[key]

    fun toMap(): Map<String, String> = data.toMap()

    companion object {
        fun fromMap(map: Map<String, String>): NodeBundle = NodeBundle(map.toMutableMap())
        val EMPTY = NodeBundle()
    }
}

@Serializable
data class RestoreData(
    val paramIdMap: Map<String, String>,
    val data: NodeBundle
) {
    fun getParamId(id: String): String = paramIdMap[id] ?: Uuid.random().toString()

    inline fun <reified T : Any> get(key: String): T? {
        return get(key, T::class)
    }

    @OptIn(InternalSerializationApi::class)
    fun <T : Any> get(key: String, clazz: KClass<T>): T? {
        val deserializer = clazz.serializer()
        return data.get(key, deserializer)
    }

    fun getString(key: String): String? = data.getString(key)

    companion object {
        val EMPTY = RestoreData(emptyMap(), NodeBundle.EMPTY)
    }
}