package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PrioritySchedulingNotAllowed
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager,
) : CreateScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule> {
        return when (priority) {
            is Priority.Plan -> {
                // TODO(Joseph): We should be using TimeManager for this instead of extension functions.
                val utcTime = priority.localTime.toLocalDateTime()
                    .toInstant(timeManager.currentTimeZone)
                    .toString()

                val schedule = Schedule(
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