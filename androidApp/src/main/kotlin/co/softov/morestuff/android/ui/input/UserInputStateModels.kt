package co.softov.morestuff.android.ui.input

import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.model.ScheduleUiModel

data class UserInputState(
  val messages: List<MessageUiModel> = listOf(),
  val scopes: List<ScopeDomain> = listOf(),
  val priority: PriorityUiModel = PriorityUiModel.Now,
  val planTime: ScheduleUiModel? = null,
  val lastCreatedTaskId: Long? = null,
)

sealed class UserInputEvent {
  data class LoadContext(val context: ChatContext): UserInputEvent()
  data object SetNowPriority : UserInputEvent()
  data object SetLaterPriority : UserInputEvent()
  data object SetPlanPriority : UserInputEvent()
  data class UpdatePlanDate(val utcTimeMillis: Long) : UserInputEvent()
  data class UpdatePlanTime(val hour: Int, val minute: Int) : UserInputEvent()
  data class SetCurrentScope(val scopeId: Long): UserInputEvent()
  data class CreateNewTask(val title: String): UserInputEvent()
}