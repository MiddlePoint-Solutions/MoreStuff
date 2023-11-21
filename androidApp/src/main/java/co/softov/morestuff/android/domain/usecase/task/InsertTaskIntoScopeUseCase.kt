package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.repository.TaskRepository

interface InsertTaskIntoScopeUseCase {
    suspend operator fun invoke(taskId: Long, scopeId: Long)
}

class InsertTaskIntoScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : InsertTaskIntoScopeUseCase {
    override suspend fun invoke(taskId: Long, scopeId: Long) {
        taskRepository.insertTaskIntoScope(taskId, scopeId)
    }
}
