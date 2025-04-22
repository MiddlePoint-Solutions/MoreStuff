package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface RemoveTasksFromScopeUseCase {
    suspend operator fun invoke(taskId: List<Uuid>, scopeId: Uuid)
}

class RemoveTasksFromScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : RemoveTasksFromScopeUseCase {
    override suspend fun invoke(taskId: List<Uuid>, scopeId: Uuid) {
        taskRepository.removeTasksFromScope(taskId, scopeId)
    }
}
