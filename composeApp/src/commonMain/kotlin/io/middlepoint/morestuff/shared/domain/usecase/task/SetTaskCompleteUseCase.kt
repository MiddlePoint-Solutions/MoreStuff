package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface SetTaskCompleteUseCase {
    suspend operator fun invoke(taskIds: List<Long>, complete: Boolean): Either<Failure, Boolean>
}

class SetTaskCompleteImpl(
    private val taskRepository: TaskRepository,
) : SetTaskCompleteUseCase {
    override suspend fun invoke(taskIds: List<Long>, complete: Boolean): Either<Failure, Boolean> {
        return taskRepository.updateTasksComplete(taskIds, complete)
    }
}