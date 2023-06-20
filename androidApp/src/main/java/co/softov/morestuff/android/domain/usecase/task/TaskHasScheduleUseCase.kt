package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface TaskHasScheduleUseCase {
    suspend operator fun invoke(taskId: Long): Boolean
}

class TaskHasScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : TaskHasScheduleUseCase {

    override suspend fun invoke(taskId: Long): Boolean {
        return scheduleRepository.taskHasSchedule(taskId)
    }
}
