package io.middlepoint.morestuff.shared.domain.model.core

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Scope(
  val id: Uuid,
  val name: String,
  val order: Int,
)

val defaultScope = Scope(
  id = "",
  name = "Stuff",
  order = 0,
)

