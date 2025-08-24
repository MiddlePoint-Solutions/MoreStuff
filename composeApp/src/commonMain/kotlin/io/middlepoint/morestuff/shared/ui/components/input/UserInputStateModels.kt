package io.middlepoint.morestuff.shared.ui.components.input

import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.PriorityUiModel
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel

data class UserInputState(
  val messages: List<MessageUiModel> = listOf(),
  val scopes: List<Scope> = listOf(),
  val priority: PriorityUiModel = PriorityUiModel.Now,
  val planTime: ScheduleUiModel? = null,
  val lastCreatedTaskId: Long? = null,
)

sealed class UserInputEvent {
  data object SetNowPriority : UserInputEvent()
  data object SetLaterPriority : UserInputEvent()
  data object SetPlanPriority : UserInputEvent()
  data class UpdatePlanDate(val utcTimeMillis: Long) : UserInputEvent()
  data class UpdatePlanTime(val hour: Int, val minute: Int) : UserInputEvent()
  data class SetCurrentScope(val scopeId: Long) : UserInputEvent()
  data class CreateNewTask(val title: String) : UserInputEvent()
}