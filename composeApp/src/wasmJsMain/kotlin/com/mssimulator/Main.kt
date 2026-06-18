package com.mssimulator

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.mssimulator.ui.App
import kotlinx.browser.document

/**
 * Web (wasmJs) entry point.
 * ComposeViewport renders into a canvas element in the browser.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val container = document.getElementById("root") ?: error("No #root element found")
    ComposeViewport(viewportContainer = container) {
        App()
    }
}
