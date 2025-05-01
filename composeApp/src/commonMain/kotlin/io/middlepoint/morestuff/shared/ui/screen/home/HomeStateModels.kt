package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@Immutable
data class HomeState(
  val currentScopeId: Uuid = Uuid(""),
  val username: String = "",
  val scopes: List<Scope> = listOf(),
  val selectedTasks: List<Uuid> = listOf(),
  val syncInProgress: Boolean = false,
  val taskInputActive: Boolean = false,
  val reorderingScopes: Map<Uuid, Boolean> = emptyMap(),
  val scheduleModel: ScheduleUiModel? = null,
  val lastCreatedTaskId: Long? = null,
  val planTime: ScheduleUiModel? = null,
  val tasks: Map<Uuid, TaskUiModel> = mapOf()
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
  data class ToggleTaskSelection(val taskId: Uuid, val task: TaskUiModel? = null) : HomeEvent()
  data class MoveSelectedTasksToScope(val scopeId: Uuid) : HomeEvent()
  data class ScopeSelected(val scopeId: Uuid) : HomeEvent()
  data class CreateScopeForSelectedTasks(val title: String) : HomeEvent()
  data class ToggleScopeReordering(val scopeId: Uuid, val isReordering: Boolean) : HomeEvent()
  data class CreateScope(val title: String) : HomeEvent()
  data class CompleteTask(val taskId: Uuid) : HomeEvent()

}
