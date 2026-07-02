package com.github.huymaster.materialweather.feature.entry.presentation

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.BaseActivity
import com.github.huymaster.materialweather.feature.entry.domain.model.EntryNavigationRoute
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
                    EntryNavigationRoute.Initial -> Unit
                    EntryNavigationRoute.MoveToInit -> TODO("INIT")
                    EntryNavigationRoute.MoveToMain -> TODO("MAIN")
                }
            }
        }

        transition.Crossfade(animationSpec = tween(350, 100)) {
            when (it) {
                EntryNavigationRoute.Initial -> LoadingScreen()
                else -> Box(Modifier.fillMaxSize())
            }
        }
    }

    private fun navigateTo(clazz: Class<out BaseActivity>) {
        val intent = Intent(this, clazz)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                modifier = Modifier.padding(36.dp, 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingIndicator()
                Spacer(Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.generic_loading),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}