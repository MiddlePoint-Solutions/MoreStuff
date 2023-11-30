package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.PriorityRepository
import co.softov.morestuff.android.domain.repository.TaskRepository

interface IncrementTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Long>
}

class IncreaseTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
    private val taskRepository: TaskRepository
) : IncrementTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Long): Either<Failure, Long> {
        val scopeId = taskRepository.currentScopeId.value
        return priorityRepository.increaseTaskPriorityScore(taskId, scopeId)
    }
}