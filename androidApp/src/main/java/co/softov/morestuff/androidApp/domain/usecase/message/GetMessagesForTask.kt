package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

interface GetMessagesForTask {
    suspend operator fun invoke(taskId: Long): SimpleResult<List<Message>>
}

class GetMessagesForTaskImpl(
    private val messageRepository: MessageRepository
) : GetMessagesForTask {
    override suspend fun invoke(taskId: Long): SimpleResult<List<Message>> {
        return messageRepository.getMessagesForTask(taskId)
    }
}