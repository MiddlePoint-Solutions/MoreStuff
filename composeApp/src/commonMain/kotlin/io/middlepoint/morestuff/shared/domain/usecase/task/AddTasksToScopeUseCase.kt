package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

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
