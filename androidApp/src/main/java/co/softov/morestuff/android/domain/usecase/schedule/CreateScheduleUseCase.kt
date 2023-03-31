package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.usecase.time.GetPriorityTimeUseCase
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val getPriorityTimeUseCase: GetPriorityTimeUseCase,
    private val timeManager: TimeManager,
) : CreateScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule> {
        val localTime = getPriorityTimeUseCase(priority)
        val utcTime =
            localTime?.toLocalDateTime()?.toInstant(timeManager.currentTimeZone)?.toString()

        val schedule = Schedule(
            id = 0,
            taskId = taskId,
            createTime = timeManager.getCreateTime(),
            scheduleLocalTime = localTime,
            scheduleUtcTime = utcTime,
            timezone = timeManager.currentTimeZone.id,
            active = true
        )
        Timber.d("### Scheduling, task $taskId = {${priority.javaClass.simpleName} -> ${schedule.scheduleLocalTime}} ###")
        return scheduleRepository.createSchedule(schedule)
    }
}