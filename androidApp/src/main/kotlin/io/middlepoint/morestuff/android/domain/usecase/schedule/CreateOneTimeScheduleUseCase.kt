package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.model.Priority
import io.middlepoint.morestuff.android.domain.model.PrioritySchedulingNotAllowed
import io.middlepoint.morestuff.android.domain.enums.ScheduleType

interface CreateOneTimeScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, ScheduleDomain>
}

class CreateOneTimeScheduleUseCaseImpl(
    private val createScheduleUseCase: CreateScheduleUseCase
) : CreateOneTimeScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, ScheduleDomain> {
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