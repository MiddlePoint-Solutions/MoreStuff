package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetHighestPriorityScoreUseCase {
    suspend operator fun invoke(): Long
}

class GetHighestPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetHighestPriorityScoreUseCase {
    override suspend fun invoke(): Long {
        return taskRepository.getHighestPriorityScore()
    }
}