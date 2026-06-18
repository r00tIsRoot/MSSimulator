package com.mssimulator.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import mssimulator.composeapp.generated.resources.Res
import mssimulator.composeapp.generated.resources.noto_sans_kr_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font as ResourceFont

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun koreanFontFamily(): FontFamily {
    val notoSansKr = ResourceFont(resource = Res.font.noto_sans_kr_regular)
    return FontFamily(notoSansKr)
}
