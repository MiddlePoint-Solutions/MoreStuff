package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetMessagesForTask {
    suspend operator fun invoke(taskId: Long): Flow<List<Message>>
}

class GetMessagesForTaskImpl(
    private val messageRepository: MessageRepository
) : GetMessagesForTask {
    override suspend fun invoke(taskId: Long): Flow<List<Message>> {
        return messageRepository.getMessagesForTask(taskId)
    }
}