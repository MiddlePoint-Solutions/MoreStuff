package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.utils.urlPattern
import java.util.regex.Pattern

interface CreateMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        contentType: ContentType,
        scheduleId: Long = 0
    ): Either<Failure, Message>
}

class CreateMessageUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase,
) : CreateMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        title: String,
        contentType: ContentType,
        scheduleId: Long
    ): Either<Failure, Message> {
        val messageResult = messageRepository.createMessage(taskId, scheduleId, contentType.value, title)

        messageResult.map { message ->
            val urlPattern = urlPattern

            val firstUrl = findFirstUrl(title, urlPattern)
            if (firstUrl != null) {
                val openGraphResult = fetchOpenGraphMetadataUseCase.invoke(firstUrl, message.id)
                if (openGraphResult != null) {
                    messageRepository.insertUrlMetadata(firstUrl, openGraphResult, message.id)
                }
            }
        }

        return messageResult
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
