package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository

interface AddTasksToScopeUseCase {
    suspend operator fun invoke(taskIds: List<Long>, scopeId: Long)
}

class AddTasksToScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : AddTasksToScopeUseCase {
    override suspend fun invoke(taskIds: List<Long>, scopeId: Long) {
        taskRepository.insertTasksIntoScope(taskIds, scopeId)
    }
}
