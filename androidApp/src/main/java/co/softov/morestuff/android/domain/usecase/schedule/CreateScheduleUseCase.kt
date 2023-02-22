package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager
) : CreateScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, Schedule> {
        val schedule = Schedule(
            id = 0,
            taskId = taskId,
            createTime = TimeUtils.getCreateTime(),
            scheduleTimeLocal = timeManager.getPriorityTime(priority),
            scheduleTimeUtc = null, // TODO:
            timezone = TimeUtils.currentTimeZone.id,
            active = true
        )
        Timber.d("### Scheduling, task $taskId = {${priority.javaClass.simpleName} -> ${schedule.scheduleTimeLocal}} ###")
        return scheduleRepository.createSchedule(schedule)
    }
}