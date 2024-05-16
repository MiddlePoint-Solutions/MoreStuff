package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.repository.TaskRepository

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
