package co.softov.morestuff.android.domain

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.data.service.TimeManagerImpl
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.OpenGraphResult
import kotlinx.datetime.*
import java.util.UUID

val timeManager = TimeManagerImpl()
fun createListOfTasks(amount: Long): List<TaskDomain> {
    return buildList {
        for (i in 1..amount) {
            add(
                createTaskForTest(
                    id = i,
                    priorityScore = i,
                    timeUtils = timeManager.nowLocalDateTimeString
                )
            )
        }
    }
}


fun createTaskForTest(
    id: Long = 1,
    title: String = "",
    timeUtils: String = timeManager.nowLocalDateTimeString,
    priorityScore: Long = 0,
    taskType: TaskType = TaskType.System,
    activeSchedule: ScheduleDomain = createScheduleForTest(
        taskId = id,
        scheduleType = ScheduleType.OneTime
    )
): TaskDomain {
    return TaskDomain(
        id = id,
        uuid = UUID.randomUUID().toString(),
        title = title,
        createTime = timeUtils,
        completeTime = null,
        priorityScore = priorityScore,
        taskType = taskType,
        schedule = listOf(activeSchedule)
    )
}


fun createListOfSchedulesForTest(amount: Long): List<ScheduleDomain> {
    return buildList {
        for (i in 1..amount) {
            add(
                createScheduleForTest(scheduleType = ScheduleType.OneTime)
            )
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
    scheduleType: ScheduleType
): ScheduleDomain {
    return ScheduleDomain(
        id,
        taskId,
        createTime,
        scheduleTimeLocal,
        scheduleTimeUtc,
        timeZone,
        active,
        scheduleType
    )
}

fun createScheduleUseCaseTest(
    id: Long = 1,
    taskId: Long = 1,
    createTime: String = "",
    scheduleTimeLocal: String = "",
    scheduleTimeUtc: String = "",
    timeZone: String = "",
    active: Boolean = true,
    scheduleType: ScheduleType = ScheduleType.OneTime
): ScheduleDomain {
    return ScheduleDomain(
        id,
        taskId,
        createTime,
        scheduleTimeLocal,
        scheduleTimeUtc,
        timeZone,
        active,
        scheduleType
    )
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
    openGraphResult: OpenGraphResult = OpenGraphResult(),
    messageData: MessageData = MessageData(
        id = 1L,
        filePath = "",
        creationTime = "",
        messageType = MessageDataType.Image
    )

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
        replyTime,
        openGraphResult,
        messageData
    )
}