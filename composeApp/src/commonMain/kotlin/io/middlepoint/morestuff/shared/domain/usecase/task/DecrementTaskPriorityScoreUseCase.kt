package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository

interface DecrementTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Uuid): Either<Failure, Long>
}

class DecreaseTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
) : DecrementTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Uuid): Either<Failure, Long> {
        return priorityRepository.decreaseTaskPriorityScore(taskId)
    }
}