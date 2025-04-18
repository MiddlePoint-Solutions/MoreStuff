package io.middlepoint.morestuff.shared.ui.screen.scopes

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope


@Immutable
data class ScopesState(
  val stuffScope: Scope? = null,
  val scopes: List<Scope> = listOf()
)


@Immutable
sealed class ScopesUiEvent {
  data class CreateScope(
    val name: String,
    val taskIds: List<Uuid> = listOf()
  ) : ScopesUiEvent()

  data class DeleteScope(val scopeId: Uuid) : ScopesUiEvent()
  data class UpdateScopeName(val scopeId: Uuid, val newName: String) : ScopesUiEvent()
  data class ReorderScopes(val scopes: List<Scope>) : ScopesUiEvent()
}
