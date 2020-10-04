package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.enums.ReplyType.CANCEL
import co.softov.morestuff.androidApp.domain.enums.ReplyType.DONE
import co.softov.morestuff.androidApp.domain.enums.ReplyType.LATER
import co.softov.morestuff.androidApp.domain.enums.ReplyType.NONE
import co.softov.morestuff.androidApp.domain.enums.ReplyType.SNOOZE
import co.softov.morestuff.androidApp.domain.enums.ReplyType.TOMORROW
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.usecase.message.GetMessagesForTask
import co.softov.morestuff.androidApp.domain.usecase.message.SetScheduleResponseMessage
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskComplete
import timber.log.Timber
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.HOUR_OF_DAY
import java.util.concurrent.TimeUnit

interface HandleScheduleResponse {
    suspend operator fun invoke(scheduleId: Long, type: ReplyType): SimpleResult<Boolean>
}

class HandleScheduleResponseImpl(
    private val getSchedule: GetSchedule,
    private val createSchedule: CreateSchedule,
    private val setScheduleResponseMessage: SetScheduleResponseMessage,
    private val getTaskMessages: GetMessagesForTask,
    private val setTaskComplete: SetTaskComplete,
    private val notifier: Notifier
) : HandleScheduleResponse {

    override suspend fun invoke(scheduleId: Long, type: ReplyType): SimpleResult<Boolean> {
        Timber.d("HandleScheduleResponse: $scheduleId, reply: $type")
        return when (val schedule = getSchedule(scheduleId)) {
            is Result.Failure -> schedule
            is Result.Success -> {
                notifier.userInteractedWithNotification(scheduleId)
                createUserReplyMessage(schedule.value, type)
            }
        }
    }

    private suspend fun createUserReplyMessage(
        schedule: Schedule,
        type: ReplyType
    ): SimpleResult<Boolean> {
        Timber.d("createUserReplyMessage: $schedule")
        when (type) {
            CANCEL -> {
                createReply(schedule.taskId, "Later!", type)
                createSchedule(schedule.taskId, 0L)
            }
            SNOOZE -> {
                createReply(schedule.taskId, "Remind me in 1 hour", type)
                val snoozeScheduleTime =
                    Calendar.getInstance().timeInMillis + TimeUnit.HOURS.toMillis(1)
                createSchedule(schedule.taskId, snoozeScheduleTime)
            }
            TOMORROW -> {
                createReply(schedule.taskId, "Remind me tomorrow!", type)
                val calendar = Calendar.getInstance()
                calendar.add(DAY_OF_YEAR, 1)
                calendar.set(HOUR_OF_DAY, 11)
                createSchedule(schedule.taskId, calendar.timeInMillis)
            }
            LATER -> {
                createReply(schedule.taskId, "Remind me later!", type)
                createSchedule(schedule.taskId, 0L)
            }
            DONE -> {
                createReply(schedule.taskId, "Done!", type)
                setTaskComplete(schedule.taskId)
            }
            NONE -> throw NotImplementedError("Should not happen!")
        }
        return Result.Success(true)
    }

    private suspend fun createReply(taskId: Long, message: String, type: ReplyType) {
        setScheduleResponseMessage(taskId, message, type)
    }

    private suspend fun showNotification(scheduleId: Long, taskId: Long): SimpleResult<Boolean> {
        return when (val result = getTaskMessages(taskId)) {
            is Result.Failure -> result
            is Result.Success -> {
                val messages = result.value
                notifier.showReminderNotificationReply(
                    scheduleId,
                    messages.subList(messages.size - 2, messages.size)
                )
                Result.Success(true)
            }
        }
    }
}