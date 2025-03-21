package io.middlepoint.morestuff.shared.domain.nav

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatScreen {
  @Serializable
  data object TaskChat : ChatScreen()

  data class ImageImport(val imageFile: PlatformFile) : ChatScreen()

  @Serializable
  data class ImagePreview(val imagePath: String, val title: String) : ChatScreen()
}