package co.softov.morestuff.android.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ChatScreen : Parcelable {

    @Parcelize
    data object TaskChat : ChatScreen()

    @Parcelize
    data class ImageImport(val uri: String) : ChatScreen()

    @Parcelize
    data class ImagePreview(val imagePath: String, val title: String) : ChatScreen()

}