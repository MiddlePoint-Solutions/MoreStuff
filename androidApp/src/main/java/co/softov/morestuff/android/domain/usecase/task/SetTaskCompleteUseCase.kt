package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.usecase.schedule.CancelActiveScheduleUseCase

interface SetTaskCompleteUseCase {
    suspend operator fun invoke(taskId: Long, complete: Boolean): Either<Failure, Boolean>
}

class SetTaskCompleteImpl(
    private val taskRepository: TaskRepository,
) : SetTaskCompleteUseCase {
    override suspend fun invoke(taskId: Long, complete: Boolean): Either<Failure, Boolean> {
        return taskRepository.updateTasksComplete(taskId, complete)
    }
}