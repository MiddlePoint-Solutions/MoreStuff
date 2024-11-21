package io.middlepoint.morestuff.shared.domain.model

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.serialization.Serializable

@Serializable
sealed class Shareable {
  data class Text(val message: String) : Shareable()

//  data class Image(val uri: String, val message: String) : Shareable()
  data class Image(val file: KmpFile, val message: String) : Shareable()


  data class Pdf(val uri: String, val message: String) : Shareable()
}
