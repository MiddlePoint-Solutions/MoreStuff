package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetLowestPriorityScoreUseCase {
    suspend operator fun invoke(): Long
}

class GetLowestPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetLowestPriorityScoreUseCase {
    override suspend fun invoke(): Long {
        return taskRepository.getLowestPriorityScore()
    }
}