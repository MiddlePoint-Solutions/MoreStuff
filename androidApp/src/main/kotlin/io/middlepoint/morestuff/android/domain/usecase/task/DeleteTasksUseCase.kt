package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.repository.TaskRepository

interface DeleteTasksUseCase {
    suspend operator fun invoke(taskIds: List<Long>): Either<Failure, Boolean>
}

class DeleteTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : DeleteTasksUseCase {
    override suspend fun invoke(taskIds: List<Long>): Either<Failure, Boolean> {
        return taskRepository.deleteTasks(taskIds)
    }
}
