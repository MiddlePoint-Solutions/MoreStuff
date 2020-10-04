package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository

interface CreateSchedule {
    suspend operator fun invoke(taskId: Long, scheduleTime: Long): SimpleResult<Long>
}

class CreateScheduleImpl(
    private val scheduleRepository: ScheduleRepository,
    private val scheduler: Scheduler
) : CreateSchedule {
    override suspend fun invoke(taskId: Long, scheduleTime: Long): SimpleResult<Long> {
        return when (val result =
            scheduleRepository.createSchedule(taskId, scheduleTime)) {
            is Result.Failure -> result
            is Result.Success -> {
                if (scheduleTime > 0) {
                    scheduler.scheduleAtExact(result.value, scheduleTime)
                }
                result
            }
        }
    }
}