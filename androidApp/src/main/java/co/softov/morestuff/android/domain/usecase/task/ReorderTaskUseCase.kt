package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface ReorderTaskUseCase {
    suspend operator fun invoke(taskId: Long, priorityScore: Long): Either<Failure, Long>
}

class ReorderTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
) : ReorderTaskUseCase {
    override suspend operator fun invoke(taskId: Long, priorityScore: Long): Either<Failure, Long> {
        return taskRepository.reorderTask(taskId, priorityScore)
    }
}