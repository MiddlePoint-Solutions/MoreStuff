package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.utils.urlPattern
import java.util.regex.Pattern


interface CreateUserTaskMessageUseCase {
    suspend operator fun invoke(task: TaskDomain): Either<Failure, Boolean>
}

class CreateUserTaskMessageUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase,
    private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase,
    private val messageRepository: MessageRepository,
) : CreateUserTaskMessageUseCase {

    override suspend fun invoke(task: TaskDomain): Either<Failure, Boolean> {
        createMessageUseCase(
            task.id,
            title = task.title,
            contentType = ContentType.TASK_MESSAGE
        ).map { message ->
            val urlPattern = urlPattern

            val firstUrl = findFirstUrl(task.title, urlPattern)
            if (firstUrl != null) {
                val openGraphResult = fetchOpenGraphMetadataUseCase.invoke(firstUrl, message.id)
                if (openGraphResult != null) {
                    messageRepository.insertUrlMetadata(firstUrl, openGraphResult, message.id)
                }
            }
        }



        return Either.Right(true)
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
