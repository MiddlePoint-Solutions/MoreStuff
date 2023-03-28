package co.softov.morestuff.android.domain

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.service.TimeManagerImpl
import kotlinx.datetime.*

val timeManager = TimeManagerImpl()
fun createListOfTasks(amount: Long): List<Task> {
    return buildList {
        for (i in 1..amount) {
            add(createTaskForTest())
        }
    }
}

fun createTaskForTest(
    id: Long = 1,
    title: String = "",
    timeUtils: String = timeManager.nowLocalDateTimeString,
): Task {
    return Task(id, title, timeUtils)
}


fun createListOfSchedulesForTest(amount: Long): List<Schedule> {
    return buildList {
        for (i in 1..amount) {
            add(createScheduleForTest())
        }
    }
}

fun createScheduleForTest(
    id: Long = 1,
    taskId: Long = 1,
    createTime: String = "",
    scheduleTimeLocal: String = "",
    scheduleTimeUtc: String = "",
    timeZone: String = "",
    active: Boolean = true,
): Schedule {
    return Schedule(id, taskId, createTime, scheduleTimeLocal, scheduleTimeUtc, timeZone, active)
}

fun createScheduleUseCaseTest(
    id: Long = 1,
    taskId: Long = 1,
    createTime: String = "",
    scheduleTimeLocal: String = "",
    scheduleTimeUtc: String = "",
    timeZone: String = "",
    active: Boolean = true,
): Schedule {
    return Schedule(id, taskId, createTime, scheduleTimeLocal, scheduleTimeUtc, timeZone, active)
        .copy(
            scheduleUtcTime = LocalDateTime.parse(scheduleTimeLocal)
                .toInstant(TimeZone.of(timeZone)).toString()
        )
}


fun createScheduleWithTitleList(amount: Long): List<ScheduleWithTitle> {
    return buildList {
        for (i in 1..amount) {
            add(createScheduleWithTitle())
        }
    }
}

fun createScheduleWithTitle(
    scheduleId: Long = 1,
    taskId: Long = 1,
    scheduleTime: String = "",
    taskTitle: String = "",
): ScheduleWithTitle {
    return ScheduleWithTitle(scheduleId, taskId, scheduleTime, taskTitle)
}


fun createListOfMessages(amount: Long): List<Message> {
    return buildList {
        for (i in 1..amount) {
            add(createMessageForTest())
        }
    }
}


fun createMessageForTest(
    id: Long = 1,
    taskId: Long = 1,
    scheduleId: Long = 1,
    contentType: ContentType = ContentType.CONFIRM_NEW_TASK,
    createTime: String = "",
    seenTime: String = "",
    content: String = "",
    replyType: ReplyType = ReplyType.DONE,
    replyContent: String = "",
    replyTime: String = "",
): Message {
    return Message(
        id,
        taskId,
        scheduleId,
        contentType,
        createTime,
        seenTime,
        content,
        replyType,
        replyContent,
        replyTime
    )
}