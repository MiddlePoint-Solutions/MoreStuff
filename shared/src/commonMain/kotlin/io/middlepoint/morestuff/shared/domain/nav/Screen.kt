package io.middlepoint.morestuff.shared.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.middlepoint.morestuff.shared.Uri
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
  data object OnBoarding : Screen()
  data object Home : Screen()
  data class Review(val scopeId: Long) : Screen()
  data object Settings : Screen()
  data class TaskChat(val taskId: Long) : Screen()
  data class Share(val shareable: Shareable, val content: String) : Screen()
  data class ImagePreview(val imageUri: String, val taskId: Long) : Screen()
  data object Scopes : Screen()
  data class CreateScope(val onSave: (String) -> Unit) : Screen()
}
