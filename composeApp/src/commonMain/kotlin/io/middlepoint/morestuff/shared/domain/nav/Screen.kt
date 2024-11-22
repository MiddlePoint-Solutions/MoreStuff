package io.middlepoint.morestuff.shared.domain.nav

import com.mohamedrejeb.calf.io.KmpFile
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
  @Serializable
  data object OnBoarding : Screen()

  @Serializable
  data object Home : Screen()

  @Serializable
  data class Review(val scopeId: Long) : Screen()

  @Serializable
  data object Settings : Screen()

  @Serializable
  data class TaskChat(val taskId: Long) : Screen()

  @Serializable
  data class Share(val shareable: Shareable, val content: String) : Screen()

  @Serializable
  data class ImagePreview(val imageUri: String, val taskId: Long) : Screen()

  @Serializable
  data object Scopes : Screen()

  @Serializable
  data class CreateScope(val onSave: (String) -> Unit) : Screen()
}
