package io.middlepoint.morestuff.shared.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ScopeDomain(
  val id: Long,
  val uid: String,
  val name: String,
  val order: Int,
)

val defaultScope = ScopeDomain(
  id = 1,
  uid = "",
  name = "Stuff",
  order = 0,
)

