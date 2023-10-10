package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Priority
import kotlinx.coroutines.delay

interface CreateHintTaskMessagesUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure, Boolean>
}

class CreateHintTaskMessagesUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase,
) : CreateHintTaskMessagesUseCase {

    private data class MessageHint(val content: String, val delay: Long)

    private val nowPriorityHints = listOf(
        MessageHint("Here you can write stuff about the task", 2300),
        MessageHint("You can also add pictures", 2300),
        MessageHint("And you can set schedule", 2300),
        MessageHint("You are ready to start!", 0)
    )

    private val laterPriorityHints = listOf(
        MessageHint("This can wait a bit, but don't forget about it.", 2300),
        MessageHint("It's not urgent, but it's important.", 2300)
    )

    private val planPriorityHints = listOf(
        MessageHint("You've planned this for later. Set a schedule!", 2300),
    )

    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure, Boolean> {
        val hints = when (priority) {
            is Priority.Now -> nowPriorityHints
            is Priority.Later -> laterPriorityHints
            is Priority.Plan -> planPriorityHints
        }
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
