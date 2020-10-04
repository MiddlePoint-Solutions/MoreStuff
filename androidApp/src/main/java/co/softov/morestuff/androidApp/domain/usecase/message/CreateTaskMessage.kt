package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

interface CreateTaskMessage {
    suspend operator fun invoke(taskId: Long, title: String): SimpleResult<Boolean>
}

class CreateTaskMessageImpl(
    private val messageRepository: MessageRepository
) : CreateTaskMessage {

    override suspend fun invoke(taskId: Long, title: String): SimpleResult<Boolean> {
        messageRepository.createTaskMessage(taskId, title)
        return Result.Success(true)
    }
}