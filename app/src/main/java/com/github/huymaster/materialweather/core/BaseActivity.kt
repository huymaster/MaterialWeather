package com.github.huymaster.materialweather.core

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.enableSavedStateHandles
import com.github.huymaster.materialweather.feature.theme.MaterialWeatherTheme
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeType
import com.github.huymaster.materialweather.feature.theme.domain.model.ThemeUiState
import com.github.huymaster.materialweather.feature.theme.presentation.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent

abstract class BaseActivity(
    private val enableEdgeToEdge: Boolean = true,
    private val enableSavedStateHandles: Boolean = true
) : FragmentActivity(), KoinComponent {
    @Composable
    abstract fun Content(adaptiveInfo: WindowAdaptiveInfo)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        if (enableEdgeToEdge) enableEdgeToEdge()
        if (enableSavedStateHandles) enableSavedStateHandles()
        setContent {
            val model = koinViewModel<ThemeViewModel>()
            val state by model.themeUiState.collectAsStateWithLifecycle()

            LaunchedEffect(state) {
                splashScreen.setKeepOnScreenCondition { state is ThemeUiState.Loading }
            }

            if (state is ThemeUiState.Success) {
                val theme = (state as ThemeUiState.Success).theme
                ContentScreen(theme)
            }
        }
    }

    @Composable
    private fun ContentScreen(theme: ThemeType) {
        val adaptiveInfo = currentWindowAdaptiveInfoV2()

        MaterialWeatherTheme(theme) {
            Surface(Modifier.fillMaxSize()) {
                Box { Content(adaptiveInfo) }
            }
        }
    }
}