package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.model.Priority
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

interface CreateTaskConfirmationMessageUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): Either<Failure,Boolean>
}

class CreateTaskConfirmationMessageUseCaseImpl(
    private val createMessageUseCase: CreateMessageUseCase
) : CreateTaskConfirmationMessageUseCase {

    private val formatter =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
    private val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

    override suspend fun invoke(taskId: Long, priority: Priority): Either<Failure,Boolean> {
        delay(1300)
        val confirmTitle = when (priority) {
            is Priority.Plan -> createLaterConfirmationTitle(priority)
            is Priority.Now -> createTodayConfirmationTitle(priority)
            is Priority.Later -> createTomorrowConfirmationTitle(priority)
        }
        createMessageUseCase(
            taskId,
            title = confirmTitle,
            contentType = ContentType.CONFIRM_NEW_TASK,
            messageData = null
        )
        return Either.Right(true)
    }

    private fun createTodayConfirmationTitle(
        today: Priority.Now
    ): String {
        return "No worries, will remind you soon!"
    }

    private fun createTomorrowConfirmationTitle(
        tomorrow: Priority.Later
    ): String {
        return "Great, remind you tomorrow!"
    }

    private fun createLaterConfirmationTitle(
        later: Priority.Plan
    ): String {
        return "Saving this for later"
    }
}