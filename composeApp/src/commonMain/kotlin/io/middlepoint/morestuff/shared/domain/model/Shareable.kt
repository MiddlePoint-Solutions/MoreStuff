package io.middlepoint.morestuff.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Shareable {
  @Serializable
  data class Text(val message: String) : Shareable()
  @Serializable
  data class Image(val uri: String, val message: String) : Shareable()
  @Serializable
  data class Pdf(val uri: String, val message: String) : Shareable()
}
