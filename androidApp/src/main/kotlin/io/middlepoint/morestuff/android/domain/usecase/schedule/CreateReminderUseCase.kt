package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.service.TimeManager
import io.middlepoint.morestuff.android.domain.enums.ScheduleType

interface CreateReminderUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, ScheduleDomain>
}

class CreateReminderUseCaseImpl(
    private val timeManager: TimeManager,
    private val createScheduleUseCase: CreateScheduleUseCase
) : CreateReminderUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, ScheduleDomain> {
        return createScheduleUseCase(
            taskId = taskId,
            scheduleType = ScheduleType.Reminder,
            localDateTime = timeManager.todayLocalDateTimeByAdding(hour = 1)
        )
    }
}