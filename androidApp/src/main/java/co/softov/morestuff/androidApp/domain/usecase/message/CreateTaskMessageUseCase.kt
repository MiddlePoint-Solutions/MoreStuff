package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

interface CreateTaskMessageUseCase {
    suspend operator fun invoke(task: Task): SimpleResult<Boolean>
}

class CreateTaskMessageUseCaseImpl(
    private val messageRepository: MessageRepository
) : CreateTaskMessageUseCase {

    override suspend fun invoke(task: Task): SimpleResult<Boolean> {
        messageRepository.createMessage(task.id, ContentType.USER_NEW_TASK.value, task.title)
        return Result.Success(true)
    }
}