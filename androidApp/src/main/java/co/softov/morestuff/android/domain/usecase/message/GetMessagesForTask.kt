package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository

interface GetMessagesForTask {
    suspend operator fun invoke(taskId: Long): Either<Failure, List<Message>>
}

class GetMessagesForTaskImpl(
    private val messageRepository: MessageRepository
) : GetMessagesForTask {
    override suspend fun invoke(taskId: Long): Either<Failure, List<Message>> {
        return messageRepository.getMessagesForTask(taskId)
    }
}