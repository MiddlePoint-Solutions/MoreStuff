package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PrioritySchedulingNotAllowed
import co.softov.morestuff.android.domain.model.ScheduleType
import timber.log.Timber

interface CreateReminderScheduleUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, ScheduleDomain>
}

class CreateReminderScheduleUseCaseImpl(
    private val timeManager: TimeManager,
    private val createScheduleUseCase: CreateScheduleUseCase
) : CreateReminderScheduleUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, ScheduleDomain> {
        return createScheduleUseCase(
            taskId = taskId,
            scheduleType = ScheduleType.Reminder,
            localDateTime = timeManager.todayLocalDateTimeByAdding(hour = 1)
        )
    }
}