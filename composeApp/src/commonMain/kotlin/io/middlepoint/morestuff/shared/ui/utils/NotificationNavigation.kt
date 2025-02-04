package io.middlepoint.morestuff.shared.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object NotificationNavigation {
  var pendingTaskIds: MutableState<Map<Long, Long>> = mutableStateOf(emptyMap())
}