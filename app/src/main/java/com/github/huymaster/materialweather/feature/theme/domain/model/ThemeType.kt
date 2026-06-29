package com.github.huymaster.materialweather.feature.theme.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ThemeType.ThemeTypeSerializer::class)
sealed interface ThemeType {
    sealed interface Dynamic : ThemeType {
        data object System : Dynamic
        data object Light : Dynamic
        data object Dark : Dynamic
    }

    sealed interface Custom : ThemeType {
        val colorArgb: Int

        data class System(override val colorArgb: Int) : Custom
        data class Light(override val colorArgb: Int) : Custom
        data class Dark(override val colorArgb: Int) : Custom
    }

    class ThemeTypeSerializer : KSerializer<ThemeType> {
        companion object {
            private const val INDEX_TYPE = 0
            private const val KEY_TYPE = "type"

            private const val INDEX_COLOR = 1
            private const val KEY_COLOR = "color"

            const val KEY_SYSTEM = "theme_system"
            const val KEY_LIGHT = "theme_light"
            const val KEY_DARK = "theme_dark"
            const val KEY_CUSTOM_SYSTEM = "theme_custom_system"
            const val KEY_CUSTOM_LIGHT = "theme_custom_light"
            const val KEY_CUSTOM_DARK = "theme_custom_dark"
        }

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ThemeType") {
            element<String>(KEY_TYPE)
            element<Int>(KEY_COLOR, isOptional = true)
        }

        override fun serialize(encoder: Encoder, value: ThemeType) {
            val composite = encoder.beginStructure(descriptor)
            val serialKey = when (value) {
                is Dynamic.System -> KEY_SYSTEM
                is Dynamic.Light -> KEY_LIGHT
                is Dynamic.Dark -> KEY_DARK
                is Custom.System -> KEY_CUSTOM_SYSTEM
                is Custom.Light -> KEY_CUSTOM_LIGHT
                is Custom.Dark -> KEY_CUSTOM_DARK
            }
            composite.encodeStringElement(descriptor, INDEX_TYPE, serialKey)
            if (value is Custom)
                composite.encodeIntElement(descriptor, INDEX_COLOR, value.colorArgb)
            composite.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): ThemeType {
            val composite = decoder.beginStructure(descriptor)
            var type: String? = null
            var color: Int? = null

            while (true) {
                when (val index = composite.decodeElementIndex(descriptor)) {
                    INDEX_TYPE -> type = composite.decodeStringElement(descriptor, INDEX_TYPE)
                    INDEX_COLOR -> color = composite.decodeIntElement(descriptor, INDEX_COLOR)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw kotlinx.serialization.SerializationException("Unknown index: $index")
                }
            }
            composite.endStructure(descriptor)

            return when (type) {
                KEY_SYSTEM -> Dynamic.System
                KEY_LIGHT -> Dynamic.Light
                KEY_DARK -> Dynamic.Dark
                KEY_CUSTOM_SYSTEM -> Custom.System(color ?: 0)
                KEY_CUSTOM_LIGHT -> Custom.Light(color ?: 0)
                KEY_CUSTOM_DARK -> Custom.Dark(color ?: 0)
                else -> Dynamic.System
            }
        }
    }
}