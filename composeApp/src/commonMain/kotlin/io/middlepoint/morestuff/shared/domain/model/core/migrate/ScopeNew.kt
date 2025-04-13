package io.middlepoint.morestuff.shared.domain.model.core.migrate

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ScopeNew(
  val id: String,
  val name: String,
  val order: Int,
)

val defaultScope = ScopeNew( // TODO: this should be created only when new user signs in
  id = "1",
  name = "Stuff",
  order = 0,
)

