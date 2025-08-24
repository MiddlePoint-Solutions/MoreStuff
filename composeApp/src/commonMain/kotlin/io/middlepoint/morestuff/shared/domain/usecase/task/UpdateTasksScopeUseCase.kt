package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface UpdateTasksScopeUseCase {
    suspend operator fun invoke(taskId: List<Uuid>, scopeId: Uuid)
}

class UpdateTasksScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : UpdateTasksScopeUseCase {
    override suspend fun invoke(taskId: List<Uuid>, scopeId: Uuid) {
        taskRepository.updateTasksScope(taskId, scopeId)
    }
}
