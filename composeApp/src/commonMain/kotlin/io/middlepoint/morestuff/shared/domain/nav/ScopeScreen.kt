package io.middlepoint.morestuff.shared.domain.nav

import io.middlepoint.morestuff.shared.domain.model.core.Scope
import kotlinx.serialization.Serializable

@Serializable
sealed class ScopeScreen {
  @Serializable
  data object Root : ScopeScreen()

  @Serializable
  data object Create : ScopeScreen()

  @Serializable
  data class Edit(val scope: Scope) : ScopeScreen()
}