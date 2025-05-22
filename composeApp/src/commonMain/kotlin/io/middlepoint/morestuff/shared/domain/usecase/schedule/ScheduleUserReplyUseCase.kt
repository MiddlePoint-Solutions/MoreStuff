package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.service.logger

interface ScheduleUserReplyUseCase {
  suspend operator fun invoke(scheduleId: Uuid, replyType: ReplyType): Either<Failure, Action?>
}

class ScheduleUserReplyUseCaseImpl(
  private val getScheduleUseCase: GetScheduleUseCase,
  private val timeManager: TimeManager,

) : ScheduleUserReplyUseCase {

  override suspend fun invoke(scheduleId: Uuid, replyType: ReplyType): Either<Failure, Action?> {
    logger.i { "🟡 Invocando ScheduleUserReplyUseCase con scheduleId=$scheduleId y replyType=$replyType" }

    return getScheduleUseCase(scheduleId).map { schedule ->
      logger.i { "🟢 Schedule obtenido: taskId=${schedule.taskId}, type=${schedule.scheduleType}" }

      when (replyType) {
        ReplyType.LATER -> {
          logger.i { "⏳ ReplyType.LATER: no se genera ninguna acción" }
          null
        }

        ReplyType.TOMORROW -> {
          val tomorrowTime = timeManager.tomorrowLocalDateTime(12)
          logger.i { "📅 ReplyType.TOMORROW: reprogramando para mañana a las 12:00 => $tomorrowTime" }
          ScheduleAction.RescheduleTaskAction(
            schedule.taskId,
            schedule.scheduleType,
            tomorrowTime
          )
        }

        ReplyType.SNOOZE -> {
          val snoozeTime = timeManager.todayLocalDateTimeByAdding(hour = 1, minute = 0)
          logger.i { "🔁 ReplyType.SNOOZE: reprogramando para dentro de 1h => $snoozeTime" }
          ScheduleAction.RescheduleTaskAction(
            schedule.taskId,
            schedule.scheduleType,
            snoozeTime
          )
        }

        ReplyType.DONE -> {
          logger.i { "✅ ReplyType.DONE: marcando como completada taskId=${schedule.taskId}" }
          TaskAction.CompleteTasksAction(listOf(schedule.taskId), true)
        }
      }
    }.also {
      logger.i { "✅ Acción generada: $it" }
    }
  }
}

