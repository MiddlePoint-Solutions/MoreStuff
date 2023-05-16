package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface DecreaseTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Long>
}

class DecreaseTaskPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository,
) : DecreaseTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Long): Either<Failure, Long> {
        return taskRepository.decreaseTaskPriorityScore(taskId)
    }
}