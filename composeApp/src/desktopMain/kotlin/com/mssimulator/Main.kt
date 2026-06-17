package com.mssimulator

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mssimulator.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MS Simulator",
        state = rememberWindowState(
            width = 1100.dp,
            height = 800.dp,
        ),
    ) {
        App()
    }
}
