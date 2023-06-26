package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskForReviewUseCase {
    suspend operator fun invoke(): Flow<List<TaskDomain>>
}

class GetTaskForReviewUseCaseImpl(
    private val taskRepository: TaskRepository,
) : GetTaskForReviewUseCase {

    override suspend fun invoke(): Flow<List<TaskDomain>> =
        taskRepository.getTasksWithoutScheduleFlow()
}
