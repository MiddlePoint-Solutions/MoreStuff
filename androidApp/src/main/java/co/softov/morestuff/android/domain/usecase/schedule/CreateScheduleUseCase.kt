package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PrioritySchedulingNotAllowed
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, ScheduleDomain>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager,
) : CreateScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, ScheduleDomain> {
        return when (priority) {
            is Priority.Plan -> {
                val utcTime = timeManager.localDateTimeStringToUtc(priority.localTime).toString()
                val schedule = ScheduleDomain(
                    id = 0,
                    taskId = taskId,
                    createTime = timeManager.getCreateTime(),
                    scheduleLocalTime = priority.localTime,
                    scheduleUtcTime = utcTime,
                    timezone = timeManager.currentTimeZone.id,
                    active = true
                )
                Timber.d("### Scheduling, task $taskId = -> ${schedule.scheduleLocalTime}} ###")
                scheduleRepository.createSchedule(schedule)
            }

            else -> Either.Left(PrioritySchedulingNotAllowed(priority))
        }
    }
}