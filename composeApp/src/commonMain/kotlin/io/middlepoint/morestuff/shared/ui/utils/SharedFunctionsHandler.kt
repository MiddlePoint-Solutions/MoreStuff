package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.runtime.Composable

expect class SharedFunctionsHandler {
  @Composable
   fun rememberReorderHapticFeedback(): ReorderHapticFeedback
}


enum class ReorderHapticFeedbackType {
  START,
  MOVE,
  END,
}

open class ReorderHapticFeedback {
  open fun performHapticFeedback(type: ReorderHapticFeedbackType) {
  }
}