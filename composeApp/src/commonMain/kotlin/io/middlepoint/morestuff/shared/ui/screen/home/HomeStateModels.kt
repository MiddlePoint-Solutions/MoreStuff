package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.defaultScope

@Immutable
data class HomeState(
  val currentScopeId: Long = defaultScope.id,
  val scopes: List<ScopeDomain> = listOf(),
  val selectedTasks: List<Long> = listOf(),
)

@Immutable
sealed class HomeEvent {
  data object ClearTaskSelection : HomeEvent()
  data object CompleteSelectedTasks : HomeEvent()
  data object DeleteSelectedTasks : HomeEvent()
  data class CreateTask(val title: String): HomeEvent()
  data class ToggleTaskSelection(val taskId: Long) : HomeEvent()
  data class MoveSelectedTasksToScope(val scopeId: Long) : HomeEvent()
  data class ScopeSelected(val scopeId: Long) : HomeEvent()
  data class CreateScopeForSelectedTasks(val title: String) : HomeEvent()
}
