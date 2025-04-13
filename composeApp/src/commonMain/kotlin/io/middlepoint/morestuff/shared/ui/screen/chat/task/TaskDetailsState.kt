package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.core.TaskDomain
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel

@Immutable
data class TaskDetailsState(
  val task: TaskDomain = TaskDomain(),
  val scheduleModel: ScheduleUiModel? = null,
  val reminderModel: ScheduleUiModel? = null,
  val taskTitle: String = ""
)


sealed class TaskDetailsEvent {
  data class UpdateTaskTitle(val title: String) : TaskDetailsEvent()
  data object ToggleTaskComplete : TaskDetailsEvent()
  data object CreateOneTimeSchedule : TaskDetailsEvent()
  data class UpdatePlanTime(val hour: Int, val minute: Int) : TaskDetailsEvent()
  data class UpdatePlanDate(val dateMillis: Long) : TaskDetailsEvent()
  data object CancelActiveSchedule : TaskDetailsEvent()
  data class ScheduleResponse(val scheduleId: Long, val replyType: ReplyType) : TaskDetailsEvent()
}
