package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.message.CreateHintTaskMessagesUseCase

interface CreateHintTaskUseCase {
    suspend operator fun invoke(params: TaskParams): TaskDomain
}


class CreateHintTaskUseCaseImpl(
    private val createTaskUseCase: CreateTaskUseCase,
    private val createHintTaskMessagesUseCase: CreateHintTaskMessagesUseCase
) : CreateHintTaskUseCase {

    override suspend fun invoke(params: TaskParams): TaskDomain {
        val createdTask = createTaskUseCase(params)

        createHintTaskMessagesUseCase(createdTask.id, params.priority )

        return createdTask
    }
}