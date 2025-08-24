package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.runtime.Composable

actual class SharedFunctionsHandler {
  @Composable
  actual fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    return ReorderHapticFeedback()
  }
}