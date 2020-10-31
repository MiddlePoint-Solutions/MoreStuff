package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

interface CreateTaskConfirmationMessageUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean>
}

class CreateTaskConfirmationMessageUseCaseImpl(
    private val messageRepository: MessageRepository
) : CreateTaskConfirmationMessageUseCase {

    private val formatter =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
    private val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean> {
        delay(1300)
        val confirmTitle = when (priority) {
            is Priority.Later -> createLaterConfirmationTitle(priority)
            is Priority.Today -> createTodayConfirmationTitle(priority)
            is Priority.Tomorrow -> createTomorrowConfirmationTitle(priority)
        }
        messageRepository.createMessage(
            taskId,
            ContentType.CONFIRM_NEW_TASK.value,
            confirmTitle
        )
        return Result.Success(true)
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