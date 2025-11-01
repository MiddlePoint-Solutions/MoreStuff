package io.middlepoint.morestuff.shared.domain.navigation

import androidx.navigation.NavType
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import io.middlepoint.morestuff.shared.domain.model.Shareable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object SignIn : AppRoute

@Serializable
data object SignInEmail : AppRoute

@Serializable
data object Home : AppRoute

@Serializable
data class Review(val scopeId: String) : AppRoute

@Serializable
data object Settings : AppRoute

@Serializable
data class ImagePreview(val imageUri: String, val taskId: String) : AppRoute

@Serializable
data object Scopes : AppRoute

@Serializable
sealed interface Import : AppRoute {
  @Serializable
  data class Text(val message: String) : Import

  @Serializable
  data class Image(val uri: String) : Import

  @Serializable
  data class Document(val uri: String) : Import
}

@Serializable
data class TaskChat(val taskId: String) : AppRoute

// TODO: change argument to file path string and construct PlatformFile from it
@Serializable
data class TaskChatImageImport(val uri: String) : AppRoute

@Serializable
data class TaskChatImagePreview(val taskId: String, val imagePath: String, val title: String) :
  AppRoute


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
