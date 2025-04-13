package io.middlepoint.morestuff.shared.domain.model.core

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Scope(
  val id: Long,
  val uid: String,
  val name: String,
  val order: Int,
)

val defaultScope = Scope(
  id = 1,
  uid = "",
  name = "Stuff",
  order = 0,
)

