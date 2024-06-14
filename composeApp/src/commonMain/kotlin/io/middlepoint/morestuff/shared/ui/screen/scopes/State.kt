package io.middlepoint.morestuff.shared.ui.screen.scopes

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain


@Immutable
data class ScopesState(
  val scopes: List<ScopeDomain> = listOf()
)


@Immutable
sealed class ScopesUiEvent {
  data class CreateScope(
    val name: String,
    val taskIds: List<Long> = listOf()
  ) : ScopesUiEvent()

  data class DeleteScope(val scopeId: Long) : ScopesUiEvent()
  data class UpdateScopeName(val scopeId: Long, val newName: String) : ScopesUiEvent()
  data class ReorderScopes(val scopes: List<ScopeDomain>) : ScopesUiEvent()
}
