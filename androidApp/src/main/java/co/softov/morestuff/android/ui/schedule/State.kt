package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.Immutable

sealed class NotificationState {
    object None : NotificationState()
    object Complete : NotificationState()
}

@Immutable
data class PriorityViewState(
    val enableConfetti: Boolean = true,
)