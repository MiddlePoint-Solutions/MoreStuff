package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.OpenGraphMetadataFetchFailure
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
import io.middlepoint.morestuff.android.ui.utils.urlPattern
import java.util.regex.Pattern

interface CheckForUrlMetadataUseCase {
    suspend operator fun invoke(
        title: String,
        messageId: Long
    ): Either<Failure, Unit>
}

class CheckForUrlMetadataUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase,
) : CheckForUrlMetadataUseCase {

    override suspend fun invoke(
        title: String,
        messageId: Long
    ): Either<Failure, Unit> {
        val urlPattern = urlPattern

        val firstUrl = findFirstUrl(title, urlPattern)
        return if (firstUrl != null) {
            val (url, isSecureUrl) = firstUrl
            if (isSecureUrl) {
                val openGraphResult = fetchOpenGraphMetadataUseCase(url)
                if (openGraphResult != null) {
                    messageRepository.insertUrlMetadata(url, openGraphResult, messageId)
                    Either.Right(Unit)
                } else {
                    Either.Left(OpenGraphMetadataFetchFailure("Failure to fetch OpenGraph metadata"))
                }
            } else {
                Either.Right(Unit)
            }
        } else {
            Either.Right(Unit)
        }
    }

    private fun findFirstUrl(content: String, urlPattern: Pattern): Pair<String, Boolean>? {
        val matcher = urlPattern.matcher(content)
        return if (matcher.find()) {
            var url = content.substring(matcher.start(), matcher.end())
            val isComplete = url.startsWith("http://") || url.startsWith("https://")
            if (!isComplete) {
                url = "https://$url"
            }
            Pair(url, isComplete)
        } else null
    }
}
