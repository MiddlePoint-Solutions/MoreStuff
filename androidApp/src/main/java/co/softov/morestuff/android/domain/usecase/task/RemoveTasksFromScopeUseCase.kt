package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository

interface RemoveTasksFromScopeUseCase {
    suspend operator fun invoke(taskId: List<Long>, scopeId: Long)
}

class RemoveTasksFromScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : RemoveTasksFromScopeUseCase {
    override suspend fun invoke(taskId: List<Long>, scopeId: Long) {
        taskRepository.removeTasksFromScope(taskId, scopeId)
    }
}
