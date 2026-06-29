package com.github.huymaster.materialweather.feature.theme.domain.model

import android.util.Log
import androidx.annotation.StringRes
import com.github.huymaster.materialweather.R
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ThemeType.ThemeTypeSerializer::class)
sealed interface ThemeType {
    @get:StringRes
    val name: Int

    data object System : ThemeType {
        override val name: Int get() = R.string.app_name
    }

    data object Light : ThemeType {
        override val name: Int get() = R.string.app_name
    }

    data object Dark : ThemeType {
        override val name: Int get() = R.string.app_name
    }

    sealed interface Custom : ThemeType {
        val colorArgb: Int

        data class Light(override val colorArgb: Int) : Custom {
            override val name: Int
                get() = R.string.app_name
        }

        data class Dark(override val colorArgb: Int) : Custom {
            override val name: Int
                get() = R.string.app_name
        }
    }

    class ThemeTypeSerializer : KSerializer<ThemeType> {
        companion object {
            const val KEY_TYPE = "type"
            const val KEY_COLOR = "color"

            const val KEY_THEME_SYSTEM = "theme_system"
            const val KEY_THEME_LIGHT = "theme_light"
            const val KEY_THEME_DARK = "theme_dark"
            const val KEY_THEME_CUSTOM_LIGHT = "theme_custom_light"
            const val KEY_THEME_CUSTOM_DARK = "theme_custom_dark"
        }

        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor(ThemeType::class.java.simpleName) {
                element<String>(KEY_TYPE)
                element<Int>(KEY_COLOR, isOptional = true)
            }

        override fun serialize(
            encoder: Encoder,
            value: ThemeType
        ) {
            val composite = encoder.beginStructure(descriptor)
            when (value) {
                System -> composite.encodeStringElement(descriptor, 0, KEY_THEME_SYSTEM)
                Light -> composite.encodeStringElement(descriptor, 0, KEY_THEME_LIGHT)
                Dark -> composite.encodeStringElement(descriptor, 0, KEY_THEME_DARK)

                is Custom -> {
                    val key = when (value) {
                        is Custom.Light -> KEY_THEME_CUSTOM_LIGHT
                        is Custom.Dark -> KEY_THEME_CUSTOM_DARK
                    }
                    composite.encodeStringElement(descriptor, 0, key)
                    composite.encodeIntElement(descriptor, 1, value.colorArgb)
                }
            }
            composite.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): ThemeType {
            val composite = decoder.beginStructure(descriptor)
            return when (val type = composite.decodeStringElement(descriptor, 0)) {
                KEY_THEME_SYSTEM -> System
                KEY_THEME_LIGHT -> Light
                KEY_THEME_DARK -> Dark
                KEY_THEME_CUSTOM_LIGHT -> Custom.Light(
                    composite.decodeIntElement(descriptor, 1)
                )

                KEY_THEME_CUSTOM_DARK -> Custom.Dark(
                    composite.decodeIntElement(descriptor, 1)
                )

                else -> {
                    Log.w(ThemeTypeSerializer::class.simpleName, "Unknown theme type: $type")
                    System
                }
            }
        }
    }
}