package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface SearchTasksUseCase {
    operator fun invoke(searchText: String): Flow<List<TaskDomain>>
}

class SearchTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : SearchTasksUseCase {
    override fun invoke(searchText: String): Flow<List<TaskDomain>> {
        return taskRepository.searchTasks(searchText)
    }
}
