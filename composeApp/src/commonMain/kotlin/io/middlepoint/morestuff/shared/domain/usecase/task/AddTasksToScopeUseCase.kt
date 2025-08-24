package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface AddTasksToScopeUseCase {
    suspend operator fun invoke(taskIds: List<Uuid>, scopeId: Uuid)
}

class AddTasksToScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : AddTasksToScopeUseCase {
    override suspend fun invoke(taskIds: List<Uuid>, scopeId: Uuid) {
        taskRepository.insertTasksIntoScope(taskIds, scopeId)
    }
}
