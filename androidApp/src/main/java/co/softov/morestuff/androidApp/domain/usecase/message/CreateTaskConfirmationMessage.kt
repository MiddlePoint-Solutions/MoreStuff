package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

interface CreateTaskConfirmationMessage {
    suspend operator fun invoke(params: ConfirmParams): SimpleResult<Boolean>
}

data class ConfirmParams(val taskId: Long, val priority: Priority)

class CreateConfirmationMessageImpl(
    private val messageRepository: MessageRepository
) : CreateTaskConfirmationMessage {

    private val formatter =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
    private val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)

    override suspend fun invoke(params: ConfirmParams): SimpleResult<Boolean> {
        delay(1300)
        val confirmTitle = when (params.priority) {
            is Priority.Later -> createLaterConfirmationTitle(params.priority)
            is Priority.Today -> createTodayConfirmationTitle(params.priority)
            is Priority.Tomorrow -> createTomorrowConfirmationTitle(params.priority)
        }
        messageRepository.createMessage(
            params.taskId,
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