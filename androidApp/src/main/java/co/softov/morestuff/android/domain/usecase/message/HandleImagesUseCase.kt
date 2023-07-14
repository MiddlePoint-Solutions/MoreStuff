package co.softov.morestuff.android.domain.usecase.message

import android.net.Uri
import co.softov.morestuff.android.domain.model.MessageWithData
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.service.TimeManager
import timber.log.Timber

interface HandleImagesUseCase {
    suspend operator fun invoke(uris: Uri, id: Long): MessageWithData
}

class HandleImagesUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val timeManager: TimeManager
) : HandleImagesUseCase {
    override suspend fun invoke(uris: Uri, id: Long): MessageWithData {
        Timber.d("IMAGE", "Received image URIs: $uris")
        val messageWithData = messageRepository.handleImages(uris, timeManager, id)
        Timber.d("HandleImagesUseCaseImpl", "Resulting MessageWithData: $messageWithData")
       return messageWithData
    }
}
