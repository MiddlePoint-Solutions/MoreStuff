package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface CreateHintTaskUseCase {
    suspend operator fun invoke(params: TaskParams): TaskDomain
}


class CreateHintTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
) : CreateHintTaskUseCase {

    override suspend fun invoke(params: TaskParams): TaskDomain = with(params) {
        val priorityScore = getDefaultPriorityScoreUseCase(priority)
        taskRepository.createTask(title, priorityScore, taskType)
    }
}