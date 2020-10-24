package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.usecase.task.GetTask

interface ExecuteSchedule {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Boolean>
}

class ExecuteScheduleImpl(
    private val messageRepository: MessageRepository,
    private val getSchedule: GetSchedule,
    private val getTask: GetTask,
    private val setScheduleFulfilled: SetScheduleFulfilled,
    private val notifier: Notifier
) : ExecuteSchedule {

    override suspend fun invoke(scheduleId: Long): SimpleResult<Boolean> {
        return when (val schedule = getSchedule(scheduleId)) {
            is Result.Failure -> schedule
            is Result.Success -> {
                setScheduleFulfilled(scheduleId)
                when (val task = getTask(schedule.value.taskId)) {
                    is Result.Failure -> task
                    is Result.Success -> createScheduleMessage(scheduleId, task.value)
                }
            }
        }
    }

    private suspend fun createScheduleMessage(
        scheduleId: Long,
        task: Task
    ): SimpleResult<Boolean> {
        return when (val message =
            messageRepository.createMessage(task.id, ContentType.TASK_REMINDER.value, task.title)) {
            is Result.Failure -> message
            is Result.Success -> {
                notifier.showScheduleNotification(scheduleId, message.value)
                Result.Success(true)
            }
        }
    }
}