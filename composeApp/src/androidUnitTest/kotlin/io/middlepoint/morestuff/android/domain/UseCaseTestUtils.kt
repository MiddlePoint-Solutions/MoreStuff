package io.middlepoint.morestuff.android.domain

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlinx.datetime.*
import java.util.UUID

val timeManager = TimeManagerImpl()
fun createListOfTasks(amount: Long): List<Task> {
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
  activeSchedule: Schedule = createScheduleForTest(
    taskId = id,
    scheduleType = ScheduleType.OneTime
  )
): Task {
    return Task(
        id = id,
        id = UUID.randomUUID().toString(),
        title = title,
        createdAt = timeUtils,
        completedAt = null,
        priorityScore = priorityScore,
        taskType = taskType,
        schedule = listOf(activeSchedule)
    )
}


fun createListOfSchedulesForTest(amount: Long): List<Schedule> {
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
): Schedule {
    return Schedule(
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
): Schedule {
    return Schedule(
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