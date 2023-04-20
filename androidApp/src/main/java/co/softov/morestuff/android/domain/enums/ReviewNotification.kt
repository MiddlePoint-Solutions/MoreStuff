package co.softov.morestuff.android.domain.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ReviewNotification : Parcelable {

    @Parcelize
    object Morning : ReviewNotification()

    @Parcelize
    object Afternoon : ReviewNotification()

    @Parcelize
    object Evening : ReviewNotification()

    @Parcelize
    data class Overload(val tasks: Int) : ReviewNotification()

}