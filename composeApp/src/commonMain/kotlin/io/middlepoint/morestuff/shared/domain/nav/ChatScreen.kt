package io.middlepoint.morestuff.shared.domain.nav

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.serialization.Serializable

@Serializable
sealed class ChatScreen {

  @Serializable
  data class Chat(val taskId: String) : ChatScreen()

  // TODO: change argument to file path string and construct PlatformFile from it
  data class Import(val imageFile: PlatformFile) : ChatScreen()

  @Serializable
  data class Preview(val imagePath: String, val title: String) : ChatScreen()
}