package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import kotlinx.datetime.LocalDateTime

sealed class ScheduleAction : Action.FeatureAction() {
    data class ExecuteScheduleAction(val scheduleId: Long) : ScheduleAction()
    data class RescheduleTaskAction(
      val taskId: Long,
      val scheduleType: ScheduleType,
      val localDateTime: LocalDateTime
    ) : ScheduleAction()

    data class CancelActiveScheduleAction(val taskId: Long) : ScheduleAction()
    data class ToggleReminderScheduleAction(val taskId: Long) : ScheduleAction()
    data class CreateReminderScheduleAction(val taskId: Long) : ScheduleAction()
    data class CancelScheduleAction(
        val taskIds: List<Long>,
        val scheduleType: List<ScheduleType> = ScheduleType.entries.toList()
    ) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: Schedule, val task: Task) : ScheduleAction()

    internal data class ScheduleReplyAction(
      val schedule: Schedule,
      val replyType: ReplyType,
    ) : ScheduleAction()

}