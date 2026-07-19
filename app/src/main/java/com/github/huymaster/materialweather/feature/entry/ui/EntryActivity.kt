package com.github.huymaster.materialweather.feature.entry.ui

import android.content.Intent
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.huymaster.materialweather.core.BaseActivity
import com.github.huymaster.materialweather.feature.entry.ui.state.EntryNavigationState
import com.github.huymaster.materialweather.feature.entry.ui.viewmodel.EntryViewModel
import com.github.huymaster.materialweather.feature.permission.ui.PermissionActivity
import org.koin.androidx.compose.koinViewModel

class EntryActivity : BaseActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content(adaptiveInfo: WindowAdaptiveInfo) {
        val model = koinViewModel<EntryViewModel>()
        val state by model.entryState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { model.check() }

        val transitionState = remember { MutableTransitionState(state) }
        val transition = rememberTransition(transitionState)
        LaunchedEffect(state) { transitionState.targetState = state }

        LaunchedEffect(transitionState.currentState, transitionState.isIdle) {
            if (transitionState.isIdle && transitionState.currentState == transitionState.targetState) {
                when (transitionState.currentState) {
                    EntryNavigationState.Initial -> Unit
                    EntryNavigationState.MoveToInit -> navigateTo(PermissionActivity::class.java)
                    EntryNavigationState.MoveToMain -> TODO("MAIN")
                }
            }
        }

        transition.Crossfade(animationSpec = tween(delayMillis = 400)) {
            when (it) {
                EntryNavigationState.Initial -> LoadingScreen()
                else -> Box(Modifier.fillMaxSize())
            }
        }
    }

    private fun navigateTo(clazz: Class<out BaseActivity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
        finish()
        applyNoAnimationTransition()
    }

    private fun applyNoAnimationTransition() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
}