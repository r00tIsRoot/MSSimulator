package com.mssimulator.font

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import mssimulator.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Loads the bundled Noto Sans KR font from compose resources by reading its bytes
 * and building a fresh [FontFamily] once the bytes are available. Returning a new
 * instance on load triggers recomposition so Korean glyphs render instead of tofu.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun koreanFontFamily(): FontFamily {
    var family by remember { mutableStateOf<FontFamily>(FontFamily.Default) }
    LaunchedEffect(Unit) {
        val bytes = Res.readBytes("font/noto_sans_kr_regular.ttf")
        family = FontFamily(Font(identity = "NotoSansKR", data = bytes))
    }
    return family
}
