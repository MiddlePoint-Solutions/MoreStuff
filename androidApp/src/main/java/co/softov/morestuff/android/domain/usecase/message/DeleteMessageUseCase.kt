package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.db.MessageQueries

interface DeleteMessageUseCase {
    suspend operator fun invoke(messageId: Long)
}

class DeleteMessageUseCaseImpl(
    private val messageQueries: MessageQueries
) : DeleteMessageUseCase {
    override suspend fun invoke(messageId: Long) {
        messageQueries.deleteMessage(messageId)
    }
}
