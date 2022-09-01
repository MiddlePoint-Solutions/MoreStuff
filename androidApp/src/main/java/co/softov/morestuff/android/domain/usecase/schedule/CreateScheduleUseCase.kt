package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.Priority
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
    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure,Schedule> {
        // TODO: Plan automatic scheduling for clashing tasks (when time option is auto)
        val scheduleTime = timeManager.getPriorityTime(priority)
        Timber.d("### Scheduling, task $taskId = {${priority.javaClass.simpleName} -> $scheduleTime} ###")
        return scheduleRepository.createSchedule(taskId, scheduleTime)
    }
}