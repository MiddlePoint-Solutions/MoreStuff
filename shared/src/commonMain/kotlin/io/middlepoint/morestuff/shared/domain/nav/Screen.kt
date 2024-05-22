package io.middlepoint.morestuff.shared.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.middlepoint.morestuff.shared.Uri
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : Parcelable {
  data object OnBoarding : Screen()

  @Parcelize
  data object Home : Screen()

  @Parcelize
  data class Review(val scopeId: Long) : Screen()

  @Parcelize
  data object Settings : Screen()

  @Parcelize
  data class TaskChat(val taskId: Long) : Screen()

  @Parcelize
  data class Share(val shareable: Shareable, val content: String) : Screen()

  @Parcelize
  data class ImagePreview(val imageUri: String, val taskId: Long) : Screen()

  @Parcelize
  data object Scopes : Screen()

  @Parcelize
  data class CreateScope(val onSave: (String) -> Unit) : Screen()
}
