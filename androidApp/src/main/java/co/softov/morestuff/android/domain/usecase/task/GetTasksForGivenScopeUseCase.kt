package co.softov.morestuff.android.domain.usecase.task


import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetTasksForGivenScopeUseCase {
    suspend operator fun invoke(scopeId: Long): List<TaskDomain>
}

class GetTasksForGivenScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : GetTasksForGivenScopeUseCase {
    override suspend fun invoke(scopeId: Long): List<TaskDomain> {
        return taskRepository.getTasksForGivenScope(scopeId)
    }
}

