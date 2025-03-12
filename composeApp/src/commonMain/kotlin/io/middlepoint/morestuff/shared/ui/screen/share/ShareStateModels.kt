package io.middlepoint.morestuff.shared.ui.screen.share

import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

data class ShareModel(
    val shareable: Shareable,
    val taskInputActive: Boolean = false,
    val currentScopeId: Long = defaultScope.id,
    val scopes: List<ScopeDomain> = listOf(),
    val searchResults: List<TaskUiModel> = listOf(),
    val createdTaskId: Long? = null,
    val scheduleModel: ScheduleUiModel? = null,
    val lastCreatedTaskId: Long? = null,
    val planTime: ScheduleUiModel? = null,
)

sealed class ShareEvent {
    data object ResetShareState: ShareEvent()
    data object ShowTaskInput: ShareEvent()
    data object ClearSearchQuery : ShareEvent()
    data class UpdateSearchQuery(val query: String) : ShareEvent()
    data class ScopeSelected(val scopeId: Long) : ShareEvent()
    data class CreateNewTask(val title: String) : ShareEvent()
    data class CreateTaskWithSchedule(val title: String) : ShareEvent()
    data class UpdatePlanTime(val hour: Int, val minute: Int) : ShareEvent()
    data class UpdatePlanDate(val dateMillis: Long) : ShareEvent()
    data object SetPlanPriority : ShareEvent()
    data object ClearPlanPriority : ShareEvent()
}