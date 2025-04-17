package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

interface CancelActiveScheduleUseCase {
  suspend operator fun invoke(
    taskIds: List<Uuid>,
    scheduleType: List<ScheduleType> = listOf()
  ): Either<Failure, List<Schedule>>
}

class CancelActiveScheduleUseCaseImpl(
  private val scheduler: Scheduler,
  private val getActiveSchedule: GetActiveSchedulesUseCase,
  private val setScheduleFulfilled: SetScheduleFulfilledUseCase
) : CancelActiveScheduleUseCase {

  override suspend fun invoke(
    taskIds: List<Uuid>,
    scheduleType: List<ScheduleType>
  ): Either<Failure, List<Schedule>> {
    if (taskIds.isEmpty()) {
      return Either.Right(emptyList())
    }

    val adjustedScheduleType = scheduleType.ifEmpty {
      listOf(ScheduleType.OneTime, ScheduleType.Reminder)
    }

    return getActiveSchedule(taskIds, adjustedScheduleType).onRight { schedules ->
      schedules.forEach { schedule ->
        setScheduleFulfilled(schedule.id)
        scheduler.cancelSchedule(Instant.parse(schedule.createdAt).toEpochMilliseconds())
      }
    }
  }
}



