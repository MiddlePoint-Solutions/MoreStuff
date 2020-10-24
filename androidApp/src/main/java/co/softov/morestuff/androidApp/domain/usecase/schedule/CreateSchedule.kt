package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import co.softov.morestuff.androidApp.domain.service.TimeManager
import timber.log.Timber

interface CreateSchedule {
    suspend operator fun invoke(taskId: Long, priority: Priority): SimpleResult<Long>
}

class CreateScheduleImpl(
    private val timeManager: TimeManager,
    private val scheduleRepository: ScheduleRepository,
    private val scheduler: Scheduler
) : CreateSchedule {
    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Long> {
        // TODO: this should be the only code that sets actual time for schedules!
        val scheduleTime = timeManager.getPriorityTime(priority) // TODO: Check if priority is time.
        Timber.d("### Scheduling, task $taskId = {${priority.javaClass.simpleName} -> $scheduleTime} ###")
        val scheduleId = scheduleRepository.createSchedule(taskId, scheduleTime)
        scheduleTime?.let { scheduler.scheduleAtExact(scheduleId, scheduleTime) }
        return Result.Success(scheduleId)
    }
}