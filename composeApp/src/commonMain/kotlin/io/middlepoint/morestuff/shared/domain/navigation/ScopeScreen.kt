package io.middlepoint.morestuff.shared.domain.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class ScopeScreen : AppRoute {
  @Serializable
  data object Root : ScopeScreen()

  @Serializable
  data object Create : ScopeScreen()

  @Serializable
  data class Edit(val scopeId: String) : ScopeScreen()
}