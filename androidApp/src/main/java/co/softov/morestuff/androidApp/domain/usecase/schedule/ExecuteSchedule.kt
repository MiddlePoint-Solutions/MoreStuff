package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import co.softov.morestuff.androidApp.domain.service.Notifier

interface ExecuteSchedule {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Boolean>
}

class ExecuteScheduleImpl(
    private val messageRepository: MessageRepository,
    private val getScheduleWithTitle: GetScheduleWithTitle,
    private val setScheduleFulfilled: SetScheduleFulfilled,
    private val notifier: Notifier
) : ExecuteSchedule {

    override suspend fun invoke(scheduleId: Long): SimpleResult<Boolean> {
        return when (val schedule = getScheduleWithTitle(scheduleId)) {
            is Result.Failure -> schedule
            is Result.Success -> {
                setScheduleFulfilled(scheduleId)
                createScheduleMessage(scheduleId, schedule.value.taskId, schedule.value.taskTitle)
            }
        }
    }

    private suspend fun createScheduleMessage(
        scheduleId: Long,
        taskId: Long,
        title: String
    ): SimpleResult<Boolean> {
        return when (val message =
            messageRepository.createMessage(taskId, ContentType.TASK_REMINDER.value, title)) {
            is Result.Failure -> message
            is Result.Success -> {
                notifier.showScheduleNotification(scheduleId, message.value)
                Result.Success(true)
            }
        }
    }
}