package com.mssimulator

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.mssimulator.ui.App

/**
 * Web (wasmJs) entry point.
 * CanvasBasedWindow renders into a canvas element in the browser.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "root") {
        App()
    }
}
