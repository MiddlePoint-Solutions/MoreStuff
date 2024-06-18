package io.middlepoint.morestuff.shared.domain.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatScreen {
  @Serializable
  data object TaskChat : ChatScreen()

  @Serializable
  data class ImageImport(val uri: String) : ChatScreen()

  @Serializable
  data class ImagePreview(val imagePath: String, val title: String) : ChatScreen()
}