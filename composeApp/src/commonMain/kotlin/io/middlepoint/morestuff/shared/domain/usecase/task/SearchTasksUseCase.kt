package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface SearchTasksUseCase {
    operator fun invoke(searchText: String, activeOnly: Boolean): Flow<List<Task>>
}

class SearchTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : SearchTasksUseCase {
    override fun invoke(searchText: String, activeOnly: Boolean): Flow<List<Task>> {
        return taskRepository.searchTasks(searchText, activeOnly)
    }
}
