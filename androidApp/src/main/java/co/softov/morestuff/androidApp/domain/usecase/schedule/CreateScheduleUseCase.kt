package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import co.softov.morestuff.androidApp.domain.service.TimeManager
import timber.log.Timber

interface CreateScheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): SimpleResult<Schedule>
}

class CreateScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager
) : CreateScheduleUseCase {
    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Schedule> {
        // TODO: this should be the only code that sets actual time for schedules!
        val scheduleTime = timeManager.getPriorityTime(priority)
        Timber.d("### Scheduling, task $taskId = {${priority.javaClass.simpleName} -> $scheduleTime} ###")
        return scheduleRepository.createSchedule(taskId, scheduleTime)
    }
}