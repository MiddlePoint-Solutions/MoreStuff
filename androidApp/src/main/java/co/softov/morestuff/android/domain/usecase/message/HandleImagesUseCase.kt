package co.softov.morestuff.android.domain.usecase.message

import android.content.Context
import android.net.Uri
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.service.TimeManager

interface HandleImagesUseCase {
    suspend operator fun invoke(uris: Uri, context: Context, id: Long)
}
class HandleImagesUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val timeManager: TimeManager
) : HandleImagesUseCase {
    override suspend fun invoke(uris: Uri, context: Context, id: Long) {
        return messageRepository.handleImages(uris, context, timeManager, id)
    }
}
