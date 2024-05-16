package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface SearchTasksUseCase {
    operator fun invoke(searchText: String, activeOnly: Boolean): Flow<List<TaskDomain>>
}

class SearchTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : SearchTasksUseCase {
    override fun invoke(searchText: String, activeOnly: Boolean): Flow<List<TaskDomain>> {
        return taskRepository.searchTasks(searchText, activeOnly)
    }
}
