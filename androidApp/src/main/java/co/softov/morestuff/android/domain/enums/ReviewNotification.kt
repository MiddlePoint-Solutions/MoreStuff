package co.softov.morestuff.android.domain.enums

sealed class ReviewNotification {
    object Morning : ReviewNotification()
    object Afternoon : ReviewNotification()
    object Evening : ReviewNotification()
    data class Overload(val tasks: Int) : ReviewNotification()
}