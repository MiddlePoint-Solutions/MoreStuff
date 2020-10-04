package co.softov.morestuff.androidApp.domain.usecase.message

import kotlinx.coroutines.delay
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        messageRepository.createConfirmationMessageForTask(params.taskId, confirmTitle)
        return Result.Success(true)
    }

    private fun createTodayConfirmationTitle(
        today: Priority.Today
    ): String {
        return if (today.time == 0L) {
            "sometime today"
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = today.time
            "remind you today at ${timeFormat.format(today.time)}"
        }
    }

    private fun createTomorrowConfirmationTitle(
        tomorrow: Priority.Tomorrow
    ): String {
        return if (tomorrow.time == 0L) {
            "see you tomorrow"
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = tomorrow.time
            "remind you tomorrow at ${timeFormat.format(tomorrow.time)}"
        }
    }

    private fun createLaterConfirmationTitle(
        later: Priority.Later
    ): String {
        return if (later.time == 0L) {
            "when later?"
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = later.time
            "remind you on ${formatter.format(calendar.time)}"
        }
    }
}