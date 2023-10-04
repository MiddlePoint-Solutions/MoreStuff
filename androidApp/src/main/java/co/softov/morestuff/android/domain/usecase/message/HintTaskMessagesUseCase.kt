package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskReorderFailure
import kotlinx.coroutines.delay

interface HintTaskMessagesUseCase {
    suspend fun invoke(taskId: Long): Either<Failure, Boolean>
}

class HintTaskMessagesUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase,
) : HintTaskMessagesUseCase {

    private data class MessageHint(val content: String, val delay: Long)

    private val hints = listOf(
        MessageHint("Here you can write stuff about the task", 2300),
        MessageHint("You can also add pictures", 2300),
        MessageHint("And you can set schedule", 2300),
        MessageHint("You are ready to start!", 0)
    )
    override suspend fun invoke(taskId: Long): Either<Failure, Boolean> {
        if (taskId != 1L) return Either.Left(TaskReorderFailure(""))

        for (hint in hints) {
            sendHint(taskId, hint.content)
            delay(hint.delay)
        }

        return Either.Right(true)
    }
    private suspend fun sendHint(taskId: Long, content: String) {
        createMessageUseCase(
            taskId = taskId,
            title = content,
            contentType = ContentType.HINT_MESSAGE,
            messageData = null
        )
    }
}
