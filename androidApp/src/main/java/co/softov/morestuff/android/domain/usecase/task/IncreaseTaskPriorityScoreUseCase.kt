package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface IncreaseTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Boolean>
}
class IncreaseTaskPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository
) : IncreaseTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Long): Either<Failure, Boolean> {
        return taskRepository.increaseTaskPriorityScore(taskId)
    }
}