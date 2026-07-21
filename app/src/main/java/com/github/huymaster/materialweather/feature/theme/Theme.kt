package com.github.huymaster.materialweather.feature.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import com.materialkolor.rememberDynamicColorScheme

private val defaultLight = lightColorScheme()
private val defaultDark = darkColorScheme()

@Composable
fun MaterialWeatherTheme(
    theme: ThemeType? = null,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit
) {
    when (theme) {
        is ThemeType.Dynamic -> DynamicTheme(theme, isAmoled, content)
        is ThemeType.Custom -> CustomTheme(theme, isAmoled, content)
        else -> DynamicTheme(ThemeType.Dynamic.System, isAmoled, content)
    }
}

@Composable
private fun DynamicTheme(
    theme: ThemeType.Dynamic,
    isAmoled: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val baseScheme: ColorScheme = remember(theme, isSystemInDarkTheme) {
        val isDark = when (theme) {
            ThemeType.Dynamic.System -> isSystemInDarkTheme
            ThemeType.Dynamic.Light -> false
            ThemeType.Dynamic.Dark -> true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (isDark) defaultDark else defaultLight
        }
    }
    val scheme = rememberDynamicColorScheme(
        baseScheme.primary,
        isSystemInDarkTheme,
        isAmoled
    )

    MaterialExpressiveTheme(
        colorScheme = scheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

@Composable
private fun CustomTheme(
    theme: ThemeType.Custom,
    isAmoled: Boolean,
    content: @Composable () -> Unit
) {
    val color = remember(theme) { theme.colorArgb }
    val isDark = when (theme) {
        is ThemeType.Custom.System -> isSystemInDarkTheme()
        is ThemeType.Custom.Light -> false
        is ThemeType.Custom.Dark -> true
    }

    val scheme: ColorScheme = rememberDynamicColorScheme(Color(color), isDark, isAmoled)
    MaterialExpressiveTheme(
        colorScheme = scheme,
        typography = MaterialTheme.typography,
        content = content
    )
}