package io.middlepoint.morestuff.shared.domain.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatScreen {
  data object TaskChat : ChatScreen()
  data class ImageImport(val uri: String) : ChatScreen()
  data class ImagePreview(val imagePath: String, val title: String) : ChatScreen()
}