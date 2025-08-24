package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.PrioritySchedulingNotAllowed
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid

interface CreateOneTimeScheduleUseCase {
    suspend operator fun invoke(taskId: Uuid, priority: Priority): Either<Failure, Schedule>
}

class CreateOneTimeScheduleUseCaseImpl(
    private val createScheduleUseCase: CreateScheduleUseCase
) : CreateOneTimeScheduleUseCase {
    override suspend fun invoke(taskId: Uuid, priority: Priority): Either<Failure, Schedule> {
        return when (priority) {
            is Priority.Plan -> {
                createScheduleUseCase(
                    taskId = taskId,
                    scheduleType = ScheduleType.OneTime,
                    localDateTime = priority.localTime
                )
            }

            else -> Either.Left(PrioritySchedulingNotAllowed(priority))
        }
    }
}