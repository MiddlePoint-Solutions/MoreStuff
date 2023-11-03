package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.Immutable

sealed class NotificationState {
    data object None : NotificationState()
    data object Complete : NotificationState()
}

@Immutable
data class PriorityViewState(
    val enableConfetti: Boolean = true,
    val taskSelectionEnabled: Boolean = false,

)