package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository

interface RemoveTaskFromScopeUseCase {
    suspend operator fun invoke(taskId: Long)
}

class RemoveTaskFromScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : RemoveTaskFromScopeUseCase {
    override suspend fun invoke(taskId: Long) {
        val scopeId = taskRepository.currentScopeId.value
        taskRepository.removeTaskFromScope(taskId, scopeId)
    }
}
