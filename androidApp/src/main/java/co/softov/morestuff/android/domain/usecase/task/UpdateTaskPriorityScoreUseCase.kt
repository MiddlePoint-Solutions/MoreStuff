package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface UpdateTaskPriorityScoreUseCase {
    suspend operator fun invoke(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long>
}

class UpdateTaskPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository,
) : UpdateTaskPriorityScoreUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long> {
        return taskRepository.updateTaskPriority(taskId, priorityScore)
    }
}