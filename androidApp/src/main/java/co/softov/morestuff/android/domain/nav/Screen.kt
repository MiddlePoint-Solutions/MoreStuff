package co.softov.morestuff.android.domain.nav

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class Screen : Parcelable {
    @Parcelize
    object Home : Screen()

    @Parcelize
    object Review : Screen()

    @Parcelize
    object Settings : Screen()

    @Parcelize
    data class TaskChat(val taskId: Long) : Screen()

    @Parcelize
    data class Share(val shareable: Shareable, val content: String) : Screen()

}
