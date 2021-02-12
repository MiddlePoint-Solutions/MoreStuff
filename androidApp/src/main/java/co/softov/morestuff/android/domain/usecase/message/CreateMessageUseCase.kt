package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.MessageRepository

interface CreateMessageUseCase {

    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        title: String,
        contentType: ContentType
    ): SimpleResult<Message>

}

class CreateMessageUseCaseImpl(
    private val messageRepository: MessageRepository
) : CreateMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        title: String,
        contentType: ContentType
    ): SimpleResult<Message> {
        return messageRepository.createMessage(taskId, scheduleId, contentType.value, title)
    }

}