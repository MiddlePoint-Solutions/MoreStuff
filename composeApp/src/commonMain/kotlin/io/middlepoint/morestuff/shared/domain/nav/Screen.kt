package io.middlepoint.morestuff.shared.domain.nav

import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

}

@Serializable
data class SignIn(val isOldUser: Boolean = false) : Screen()

@Serializable
data object SignInEmail : Screen()

@Serializable
object Home : Screen()

@Serializable
data class Review(val scopeId: Uuid) : Screen()

@Serializable
data object Settings : Screen()

@Serializable
data class TaskChat(val taskId: Uuid) : Screen()

@Serializable
data class Share(val shareable: Shareable, val content: String) : Screen()

@Serializable
data class ImagePreview(val imageUri: String, val taskId: Uuid) : Screen()

@Serializable
data object Scopes : Screen()

@Serializable
data class CreateScope(val onSave: (String) -> Unit) : Screen()
