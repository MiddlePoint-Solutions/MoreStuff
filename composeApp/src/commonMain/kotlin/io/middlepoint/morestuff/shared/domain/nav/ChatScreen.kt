package io.middlepoint.morestuff.shared.domain.nav

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatScreen {
  @Serializable
  data object TaskChat : ChatScreen()

  data class ImageImport(val imageFile: KmpFile) : ChatScreen()

  @Serializable
  data class ImagePreview(val imagePath: String, val title: String) : ChatScreen()
}