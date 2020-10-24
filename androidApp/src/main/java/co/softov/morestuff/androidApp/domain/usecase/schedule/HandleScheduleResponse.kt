package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.enums.ReplyType.*
import co.softov.morestuff.androidApp.domain.enums.TimeOption
import co.softov.morestuff.androidApp.domain.enums.TimeOption.*
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.usecase.message.GetMessagesForTask
import co.softov.morestuff.androidApp.domain.usecase.message.SetScheduleResponseMessage
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskComplete
import timber.log.Timber

interface HandleScheduleResponse {
    suspend operator fun invoke(scheduleId: Long, replyType: ReplyType): SimpleResult<Boolean>
}

class HandleScheduleResponseImpl(
    private val getSchedule: GetSchedule,
    private val createSchedule: CreateSchedule,
    private val setScheduleResponseMessage: SetScheduleResponseMessage,
    private val getTaskMessages: GetMessagesForTask,
    private val setTaskComplete: SetTaskComplete,
    private val notifier: Notifier
) : HandleScheduleResponse {

    override suspend fun invoke(scheduleId: Long, replyType: ReplyType): SimpleResult<Boolean> {
        Timber.d("HandleScheduleResponse: $scheduleId, reply: $replyType")
        return when (val schedule = getSchedule(scheduleId)) {
            is Result.Failure -> schedule
            is Result.Success -> {
                notifier.userInteractedWithNotification(scheduleId)
                createUserReplyMessage(schedule.value.taskId, replyType)
            }
        }
    }

    private suspend fun createUserReplyMessage(
        taskId: Long,
        type: ReplyType
    ): SimpleResult<Boolean> {
        Timber.d("createUserReplyMessage: $taskId = $type")
        when (type) {
            SNOOZE -> {
                createReply(taskId, "Remind me in 1 hour", type)
                createSchedule(taskId, Priority.Today(Default))
            }
            TOMORROW -> {
                createReply(taskId, "Remind me tomorrow!", type)
                createSchedule(taskId, Priority.Tomorrow(Default))
            }
            LATER -> {
                createReply(taskId, "Remind me later!", type)
                createSchedule(taskId, Priority.Later(Default))
            }
            DONE -> {
                createReply(taskId, "Done!", type)
                setTaskComplete(taskId)
            }
        }
        return Result.Success(true)
    }

    private suspend fun createReply(taskId: Long, message: String, type: ReplyType) {
        setScheduleResponseMessage(taskId, message, type)
    }
}