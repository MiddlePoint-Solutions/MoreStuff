package co.softov.morestuff.android.ui.model

sealed class NotificationState {
    data object None : NotificationState()
    data object Complete : NotificationState()
}