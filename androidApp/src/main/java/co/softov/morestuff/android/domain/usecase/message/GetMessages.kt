package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetMessages {
    operator fun invoke(taskId: Long? = null): Flow<List<Message>>
}

class GetMessagesImpl(
    private val messageRepository: MessageRepository,
    private val getMessagesForTask: GetTaskMessagesFlowUseCase,
) : GetMessages {
    override fun invoke(taskId: Long?): Flow<List<Message>> {
        return when {
            taskId != null -> getMessagesForTask(taskId)
            else -> messageRepository.getAllMessages()
        }
    }
} 



