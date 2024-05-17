package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface UpdateTasksScopeUseCase {
    suspend operator fun invoke(taskId: List<Long>, scopeId: Long)
}

class UpdateTasksScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : UpdateTasksScopeUseCase {
    override suspend fun invoke(taskId: List<Long>, scopeId: Long) {
        taskRepository.updateTasksScope(taskId, scopeId)
    }
}
