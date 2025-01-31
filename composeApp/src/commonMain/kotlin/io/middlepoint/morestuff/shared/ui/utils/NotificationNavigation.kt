package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.runtime.MutableState

expect object NotificationState {
  var pendingTaskId: MutableState<Long?>
}