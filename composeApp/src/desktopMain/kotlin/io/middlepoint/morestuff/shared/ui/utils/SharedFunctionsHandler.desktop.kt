package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class SharedFunctionsHandler {
  @Composable
  actual fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    return remember { ReorderHapticFeedback() }
  }
}