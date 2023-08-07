package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.ScheduleType

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