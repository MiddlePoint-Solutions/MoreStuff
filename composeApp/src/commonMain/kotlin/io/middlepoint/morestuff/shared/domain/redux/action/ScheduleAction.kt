package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
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

    internal data class ScheduleCreatedAction(val schedule: ScheduleDomain, val task: TaskDomain) : ScheduleAction()

    internal data class ScheduleReplyAction(
      val schedule: ScheduleDomain,
      val replyType: ReplyType,
    ) : ScheduleAction()

}