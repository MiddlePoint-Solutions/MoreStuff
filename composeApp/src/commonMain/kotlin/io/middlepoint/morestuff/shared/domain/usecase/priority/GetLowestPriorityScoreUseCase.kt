package io.middlepoint.morestuff.shared.domain.usecase.priority

import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository

interface GetLowestPriorityScoreUseCase {
    suspend operator fun invoke(): Long
}

class GetLowestPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository
) : GetLowestPriorityScoreUseCase {
    override suspend fun invoke(): Long {
        return priorityRepository.getLowestPriorityScore()
    }
}