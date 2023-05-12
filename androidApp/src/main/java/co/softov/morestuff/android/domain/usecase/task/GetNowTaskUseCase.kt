package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetNowTaskUseCase {
    operator fun invoke(): Flow<List<TaskDomain>>
}

class GetNowTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetNowTaskUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getNowTasksFlow()
    }
}