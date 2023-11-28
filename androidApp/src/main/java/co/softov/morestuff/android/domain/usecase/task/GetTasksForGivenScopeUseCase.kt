package co.softov.morestuff.android.domain.usecase.task


import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import timber.log.Timber

interface GetTasksForGivenScopeUseCase {
    suspend operator fun invoke(scopeId: Long): List<TaskDomain>
}

class GetTasksForGivenScopeUseCaseImpl(
    private val taskRepository: TaskRepository,
) : GetTasksForGivenScopeUseCase {
    override suspend fun invoke(scopeId: Long): List<TaskDomain> {
        val tasks = taskRepository.getTasksForGivenScope(scopeId)
        Timber.d(" Scope Tareas recuperadas para el scopeId $scopeId: ${tasks.size} tareas encontradas")
        return tasks
    }
}

