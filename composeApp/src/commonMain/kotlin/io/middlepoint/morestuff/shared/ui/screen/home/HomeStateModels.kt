package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.core.defaultScope
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@Immutable
data class HomeState(
  val currentScopeId: Long = defaultScope.id,
  val scopes: List<ScopeDomain> = listOf(),
  val selectedTasks: List<Long> = listOf(),
  val taskInputActive: Boolean = false,
  val reorderingScopes: Map<Long, Boolean> = emptyMap(),
  val scheduleModel: ScheduleUiModel? = null,
  val lastCreatedTaskId: Long? = null,
  val planTime: ScheduleUiModel? = null,
  val tasks: Map<Long, TaskUiModel> = mapOf()
  )

@Immutable
sealed class HomeEvent {
  data object ResetHomeState : HomeEvent()
  data object HideTaskInput : HomeEvent()
  data object ShowTaskInput: HomeEvent()
  data object CompleteSelectedTasks : HomeEvent()
  data object DeleteSelectedTasks : HomeEvent()
  data class CreateTask(val title: String): HomeEvent()
  data class CreateTaskWithSchedule(val title: String) : HomeEvent()
  data class UpdatePlanTime(val hour: Int, val minute: Int) : HomeEvent()
  data class UpdatePlanDate(val dateMillis: Long) : HomeEvent()
  data object SetPlanPriority : HomeEvent()
  data object ClearPlanPriority : HomeEvent()
  data class ToggleTaskSelection(val taskId: Long, val task: TaskUiModel? = null) : HomeEvent()
  data class MoveSelectedTasksToScope(val scopeId: Long) : HomeEvent()
  data class ScopeSelected(val scopeId: Long) : HomeEvent()
  data class CreateScopeForSelectedTasks(val title: String) : HomeEvent()
  data class ToggleScopeReordering(val scopeId: Long, val isReordering: Boolean) : HomeEvent()
  data class CreateScope(val title: String) : HomeEvent()
  data class CompleteTask(val taskId: Long) : HomeEvent()

}
