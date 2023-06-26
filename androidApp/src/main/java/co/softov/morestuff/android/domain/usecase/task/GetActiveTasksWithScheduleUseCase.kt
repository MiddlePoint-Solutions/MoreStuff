package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow


interface GetActiveTasksWithScheduleUseCase {
    suspend operator fun invoke(): Flow<List<TaskDomain>>
}


class GetActiveTasksWithScheduleUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksWithScheduleUseCase {
    override suspend operator fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getActiveTasksWithScheduleFlow()
    }
}
