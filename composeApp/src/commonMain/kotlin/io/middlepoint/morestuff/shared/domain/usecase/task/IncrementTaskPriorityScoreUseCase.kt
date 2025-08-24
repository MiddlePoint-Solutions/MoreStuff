package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository

interface IncrementTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Uuid): Either<Failure, Long>
}

class IncreaseTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
) : IncrementTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Uuid): Either<Failure, Long> {
        return priorityRepository.increaseTaskPriorityScore(taskId)
    }
}