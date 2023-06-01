package co.softov.morestuff.android.ui.schedule

sealed class NotificationState {
    object None : NotificationState()
    object Complete : NotificationState()
}