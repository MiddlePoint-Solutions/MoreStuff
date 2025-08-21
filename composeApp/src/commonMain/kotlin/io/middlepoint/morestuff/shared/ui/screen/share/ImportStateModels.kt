package io.middlepoint.morestuff.shared.ui.screen.share

import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

data class ImportModel(
  val shareable: Shareable,
  val currentScopeId: Uuid? = null,
  val taskInputActive: Boolean = false,
  val scopes: List<Scope> = listOf(),
  val searchResults: List<TaskUiModel> = listOf(),
  val createdTaskId: Uuid? = null,
  val scheduleModel: ScheduleUiModel? = null,
  val lastCreatedTaskId: Uuid? = null,
  val planTime: ScheduleUiModel? = null,
)

sealed class ImportEvent {
    data object ResetShareState: ImportEvent()
    data object ShowTaskInput: ImportEvent()
    data object ClearSearchQuery : ImportEvent()
    data class UpdateSearchQuery(val query: String) : ImportEvent()
    data class ScopeSelected(val scopeId: Uuid) : ImportEvent()
    data class CreateNewTask(val title: String) : ImportEvent()
    data class CreateTaskWithSchedule(val title: String) : ImportEvent()
    data class UpdatePlanTime(val hour: Int, val minute: Int) : ImportEvent()
    data class UpdatePlanDate(val dateMillis: Long) : ImportEvent()
    data object SetPlanPriority : ImportEvent()
    data object ClearPlanPriority : ImportEvent()
}