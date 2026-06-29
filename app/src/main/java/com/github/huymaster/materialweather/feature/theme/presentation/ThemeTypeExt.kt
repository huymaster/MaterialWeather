package com.github.huymaster.materialweather.feature.theme.presentation

import androidx.annotation.StringRes
import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType

@get:StringRes
val ThemeType.name: Int
    get() = when (this) {
        is ThemeType.Dynamic.System -> R.string.theme_dynamic_system
        is ThemeType.Dynamic.Light -> R.string.theme_dynamic_light
        is ThemeType.Dynamic.Dark -> R.string.theme_dynamic_dark
        is ThemeType.Custom.System -> R.string.theme_custom_system
        is ThemeType.Custom.Light -> R.string.theme_custom_light
        is ThemeType.Custom.Dark -> R.string.theme_custom_dark
    }