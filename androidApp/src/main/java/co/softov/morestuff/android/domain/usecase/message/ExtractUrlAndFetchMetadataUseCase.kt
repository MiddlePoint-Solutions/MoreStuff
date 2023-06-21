package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.OpenGraphMetadataFetchFailure
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.utils.urlPattern
import java.util.regex.Pattern

interface ExtractUrlAndFetchMetadataUseCase {
    suspend operator fun invoke(
        title: String,
        messageId: Long
    ): Either<Failure, Unit>
}

class ExtractUrlAndFetchMetadataUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase,
) : ExtractUrlAndFetchMetadataUseCase {

    override suspend fun invoke(
        title: String,
        messageId: Long
    ): Either<Failure, Unit> {
        val urlPattern = urlPattern

        val firstUrl = findFirstUrl(title, urlPattern)
        return if (firstUrl != null) {
            val openGraphResult = fetchOpenGraphMetadataUseCase(firstUrl, messageId)
            if (openGraphResult != null) {
                messageRepository.insertUrlMetadata(firstUrl, openGraphResult, messageId)
                Either.Right(Unit)
            } else {
                Either.Left(OpenGraphMetadataFetchFailure("Failure to fetch OpenGraph metadata"))
            }
        } else {
            Either.Right(Unit)
        }
    }
}

fun findFirstUrl(content: String, urlPattern: Pattern): String? {
    val matcher = urlPattern.matcher(content)
    return if (matcher.find()) {
        var url = content.substring(matcher.start(), matcher.end())
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        url
    } else null
}
