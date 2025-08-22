package io.middlepoint.morestuff.shared.domain.nav

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed class Screen

@Serializable
data class SignIn(val isOldUser: Boolean = false) : Screen()

@Serializable
data object SignInEmail : Screen()

@Serializable
data object Home : Screen()

@Serializable
data class Review(val scopeId: String) : Screen()

@Serializable
data object Settings : Screen()

@Serializable
data class TaskChat(val taskId: String) : Screen()

@Serializable
data class ImagePreview(val imageUri: String, val taskId: String) : Screen()

@Serializable
data object Scopes : Screen()

@Serializable
sealed class Import: Screen() {
  @Serializable
  data class Text(val message: String) : Import()

  @Serializable
  data class Image(val uri: String) : Import()

  @Serializable
  data class Document(val uri: String) : Import()
}


data object ShareableNavType : NavType<Shareable>(isNullableAllowed = false) {
  override fun put(
    bundle: SavedState,
    key: String,
    value: Shareable
  ) {
    bundle.write { putString(key, Json.encodeToString(value)) }
  }

  override fun get(
    bundle: SavedState,
    key: String
  ): Shareable? {
    return bundle.read { Json.decodeFromString(getString(key)) }
  }

  override fun parseValue(value: String): Shareable {
    return Json.decodeFromString(value)
  }
}
