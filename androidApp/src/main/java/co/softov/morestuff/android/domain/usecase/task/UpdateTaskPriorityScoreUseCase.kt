package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.PriorityRepository

interface UpdateTaskPriorityScoreUseCase {
    suspend operator fun invoke(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long>
}

class UpdateTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
) : UpdateTaskPriorityScoreUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        priorityScore: Long
    ): Either<Failure, Long> {
        return priorityRepository.updateTaskPriority(taskId, priorityScore)
    }
}