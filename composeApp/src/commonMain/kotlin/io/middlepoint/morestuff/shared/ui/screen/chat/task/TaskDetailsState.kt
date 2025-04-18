package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@Immutable
data class TaskDetailsState(
  val task: TaskUiModel = TaskUiModel(),
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
  data class ScheduleResponse(val scheduleId: Uuid, val replyType: ReplyType) : TaskDetailsEvent()
}
