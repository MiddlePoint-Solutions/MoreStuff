package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface IncrementTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Long>
}

class IncreaseTaskPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository,
) : IncrementTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Long): Either<Failure, Long> {
        return taskRepository.increaseTaskPriorityScore(taskId)
    }
}