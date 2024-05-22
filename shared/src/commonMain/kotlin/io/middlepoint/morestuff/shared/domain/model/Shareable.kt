package io.middlepoint.morestuff.shared.domain.model

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.serialization.Serializable

@Serializable
sealed class Shareable : Parcelable {
  data class Text(val message: String) : Shareable()

  data class Image(val uris: String, val message: String) : Shareable()

  data class Pdf(val uris: String, val message: String) : Shareable()
}
