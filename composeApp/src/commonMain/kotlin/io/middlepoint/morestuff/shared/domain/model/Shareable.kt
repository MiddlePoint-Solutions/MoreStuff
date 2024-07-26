package io.middlepoint.morestuff.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Shareable {
  data class Text(val message: String) : Shareable()

  data class Image(val uri: String, val message: String) : Shareable()

  data class Pdf(val uri: String, val message: String) : Shareable()
}
