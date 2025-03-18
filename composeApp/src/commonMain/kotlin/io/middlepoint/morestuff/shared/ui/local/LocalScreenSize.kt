package io.middlepoint.morestuff.shared.ui.local

import androidx.compose.runtime.compositionLocalOf
import io.middlepoint.morestuff.shared.ui.utils.ScreenSizeInfo

val LocalScreenSize = compositionLocalOf<ScreenSizeInfo> { error("No Screen Size Info provided") }