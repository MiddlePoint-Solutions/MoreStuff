package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.PriorityRepository

interface DecrementTaskPriorityScoreUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Long>
}

class DecreaseTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
) : DecrementTaskPriorityScoreUseCase {
    override suspend operator fun invoke(taskId: Long): Either<Failure, Long> {
        return priorityRepository.decreaseTaskPriorityScore(taskId)
    }
}