package co.softov.morestuff.android.nav

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
    data class TaskChat(val taskId: Long) : Screen() {
        override fun equals(other: Any?): Boolean {
            return other?.let { it is TaskChat && it.taskId == taskId } ?: false
        }

        override fun hashCode(): Int {
            return taskId.hashCode()
        }
    }
    @Parcelize
    data class Share(val shareable: Shareable, val content: String) : Screen()

}

enum class Shareable {
    Text, Image
}