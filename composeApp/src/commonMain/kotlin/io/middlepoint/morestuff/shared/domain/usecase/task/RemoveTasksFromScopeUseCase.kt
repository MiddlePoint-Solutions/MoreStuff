package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

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
