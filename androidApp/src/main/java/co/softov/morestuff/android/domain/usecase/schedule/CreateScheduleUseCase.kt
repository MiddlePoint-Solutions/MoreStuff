package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager
) : CreateScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule> {
        // TODO: This should change after merging the GetPriorityTimeUseCase changes
        val localTime = timeManager.getPriorityTime(priority)
        val utcTime = localTime?.toLocalDateTime()?.toInstant(TimeUtils.currentTimeZone)?.toString()
        val schedule = Schedule(
            id = 0,
            taskId = taskId,
            createTime = TimeUtils.getCreateTime(),
            scheduleLocalTime = localTime,
            scheduleUtcTime = utcTime,
            timezone = TimeUtils.currentTimeZone.id,
            active = true
        )
        Timber.d("### Scheduling, task $taskId = {${priority.javaClass.simpleName} -> ${schedule.scheduleLocalTime}} ###")
        return scheduleRepository.createSchedule(schedule)
    }
}