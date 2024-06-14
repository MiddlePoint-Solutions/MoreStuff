package io.middlepoint.morestuff.shared.domain.usecase.priority

import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository

interface GetHighestPriorityScoreUseCase {
    suspend operator fun invoke(): Long
}

class GetHighestPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository
) : GetHighestPriorityScoreUseCase {
    override suspend fun invoke(): Long {
        return priorityRepository.getHighestPriorityScore()
    }
}