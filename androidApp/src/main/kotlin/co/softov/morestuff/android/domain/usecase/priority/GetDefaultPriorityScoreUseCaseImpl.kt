package co.softov.morestuff.android.domain.usecase.priority

import co.softov.morestuff.android.domain.model.Priority

interface GetDefaultPriorityScoreUseCase {
    suspend operator fun invoke(priority: Priority): Long
}

class GetDefaultPriorityScoreUseCaseImpl(
    private val getLowestPriorityScoreUseCase: GetLowestPriorityScoreUseCase,
    private val getHighestPriorityScoreUseCase: GetHighestPriorityScoreUseCase,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase,
) : GetDefaultPriorityScoreUseCase {
    override suspend fun invoke(priority: Priority): Long {
        return when (priority) {
            is Priority.Now -> getHighestPriorityScoreUseCase() + 1
            is Priority.Later -> getLowestPriorityScoreUseCase() - 1
            is Priority.Plan -> getPlanPriorityScoreUseCase(priority.localTime)
        }
    }
}