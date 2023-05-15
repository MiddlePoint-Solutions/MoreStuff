package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetLaterTaskUseCase {
    operator fun invoke(): Flow<List<TaskDomain>>
}

class GetLaterTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetLaterTaskUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getLaterTasksFlow()
    }
}