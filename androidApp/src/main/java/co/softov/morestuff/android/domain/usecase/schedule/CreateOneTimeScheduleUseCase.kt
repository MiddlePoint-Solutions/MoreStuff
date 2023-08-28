package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PrioritySchedulingNotAllowed
import co.softov.morestuff.android.domain.enums.ScheduleType

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