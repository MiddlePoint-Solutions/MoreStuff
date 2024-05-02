package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

interface GetLastMessageFlowUseCase {
    operator fun invoke(contentType: ContentType): Flow<Message?>
}

class GetLastMessageFlowUseCaseImpl(
    private val messageRepository: MessageRepository
) : GetLastMessageFlowUseCase {
    override fun invoke(contentType: ContentType): Flow<Message?> {
        Timber.d("keymessage use case message for contentType: ${contentType.value}")
        return messageRepository.getLastMessageFlow(contentType)
    }
}