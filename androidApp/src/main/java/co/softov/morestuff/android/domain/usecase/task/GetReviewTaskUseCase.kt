package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetReviewTaskUseCase {
    suspend operator fun invoke(): Flow<List<TaskDomain>>
}

class GetReviewTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetReviewTaskUseCase {

    override suspend fun invoke(): Flow<List<TaskDomain>> =
        taskRepository.getActiveTasksFlow()
}
