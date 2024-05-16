package io.middlepoint.morestuff.android.domain.usecase.priority

import io.middlepoint.morestuff.android.domain.repository.PriorityRepository

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