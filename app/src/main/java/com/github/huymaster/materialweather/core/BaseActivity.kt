package com.github.huymaster.materialweather.core

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.enableSavedStateHandles

abstract class BaseActivity(
    private val enableEdgeToEdge: Boolean = true,
    private val enableSavedStateHandles: Boolean = true
) : FragmentActivity() {
    @Composable
    abstract fun Content(adaptiveInfo: WindowAdaptiveInfo)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initialize()
    }

    private fun initialize() {
        if (enableEdgeToEdge) enableEdgeToEdge()
        if (enableSavedStateHandles) enableSavedStateHandles()
        setContent(null, ::Root)
    }

    @Composable
    private fun Root() {
        val adaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfoV2()

        Content(adaptiveInfo)
    }
}