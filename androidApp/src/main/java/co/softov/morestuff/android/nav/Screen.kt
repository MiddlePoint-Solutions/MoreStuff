package co.softov.morestuff.android.nav

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
sealed class Screen : Parcelable {
    object Home : Screen()
    data class TaskChat(val taskId: Long) : Screen()
    object Review : Screen()
    object Settings : Screen()
}