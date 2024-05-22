package io.middlepoint.morestuff.shared.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatScreen : Parcelable {
  data object TaskChat : ChatScreen()
  data class ImageImport(val uri: String) : ChatScreen()
  data class ImagePreview(val imagePath: String, val title: String) : ChatScreen()

}