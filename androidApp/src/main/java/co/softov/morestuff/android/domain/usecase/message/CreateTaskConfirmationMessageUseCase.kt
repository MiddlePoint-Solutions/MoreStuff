package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.Priority
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
            is Priority.Later -> createLaterConfirmationTitle(priority)
            is Priority.Today -> createTodayConfirmationTitle(priority)
            is Priority.Tomorrow -> createTomorrowConfirmationTitle(priority)
        }
        createMessageUseCase(
            taskId,
            title = confirmTitle,
            contentType = ContentType.CONFIRM_NEW_TASK
        )
        return Either.Right(true)
    }

    private fun createTodayConfirmationTitle(
        today: Priority.Today
    ): String {
        return "No worries, will remind you soon!"
    }

    private fun createTomorrowConfirmationTitle(
        tomorrow: Priority.Tomorrow
    ): String {
        return "Great, remind you tomorrow!"
    }

    private fun createLaterConfirmationTitle(
        later: Priority.Later
    ): String {
        return "Saving this for later"
    }
}