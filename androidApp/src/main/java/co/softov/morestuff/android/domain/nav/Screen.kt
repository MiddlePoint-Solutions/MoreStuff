package co.softov.morestuff.android.domain.nav

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class Screen : Parcelable {

    @Parcelize
    object OnBoarding : Screen()

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

    @Parcelize
    object AboutLibraries : Screen()

    @Parcelize
    data class ImagePreview(val imageUri: Uri, val taskId: Long) : Screen()

}
