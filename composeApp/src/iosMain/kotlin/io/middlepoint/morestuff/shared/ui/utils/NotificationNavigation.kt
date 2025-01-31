package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


actual object NotificationState {
  actual var pendingTaskId: MutableState<Long?> = mutableStateOf(null)
}