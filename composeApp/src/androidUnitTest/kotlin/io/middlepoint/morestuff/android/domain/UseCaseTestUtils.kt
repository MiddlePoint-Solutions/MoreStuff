package io.middlepoint.morestuff.android.domain

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlinx.datetime.*
import java.util.UUID

val timeManager = TimeManagerImpl()
fun createListOfTasks(amount: Long): List<Task> {
  return buildList {
    for (i in 1..amount) {
      add(
        createTaskForTest(
          priorityScore = i,
        )
      )
    }
  }
}


fun createTaskForTest(
  id: Uuid = Uuid.generate(),
  title: String = "Task ${id.value}",
  priorityScore: Long = 0,
  activeSchedule: Schedule = createScheduleForTest(
    taskId = id,
    scheduleType = ScheduleType.OneTime
  )
): Task {
  return Task(
    id = Uuid("id"),
    title = title,
    createdAt = timeManager.nowUtcInstant.toString(),
    updatedAt = timeManager.nowUtcInstant.toString(),
    completedAt = null,
    timezone = null,
    priorityScore = priorityScore,
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
  id: Uuid = Uuid.generate(),
  taskId: Uuid = Uuid.generate(),
  createTime: String = "",
  scheduleTimeLocal: String = "",
  scheduleTimeUtc: String = "",
  timeZone: String = "",
  active: Boolean = true,
  scheduleType: ScheduleType
): Schedule {
  return Schedule(
    Uuid("$id"),
    Uuid("$taskId"),
    createTime,
    scheduleTimeLocal,
    scheduleTimeUtc,
    timeZone,
    active,
    scheduleType
  )
}

fun createScheduleUseCaseTest(
  id: Uuid = Uuid.generate(),
  taskId: Uuid = Uuid.generate(),
  createTime: String = "",
  scheduleTimeLocal: String = "",
  scheduleTimeUtc: String = "",
  timeZone: String = TimeZone.currentSystemDefault().id,
  active: Boolean = true,
  scheduleType: ScheduleType = ScheduleType.OneTime
): Schedule {
  return Schedule(
    Uuid("$id"),
    Uuid("$taskId"),
    createTime,
    scheduleTimeLocal,
    scheduleTimeUtc,
    timeZone,
    active,
    scheduleType
  )
    .copy(
      scheduledAt = LocalDateTime.parse(scheduleTimeLocal)
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
  id: Uuid = Uuid.generate(),
  taskId: Uuid = Uuid.generate(),
  scheduleId: Uuid = Uuid.generate(),
  contentType: ContentType = ContentType.CONFIRM_NEW_TASK,
  content: String = "",
  openGraphResult: OpenGraphResult = OpenGraphResult(),
  messageExtra: MessageExtra = MessageExtra(
    Uuid("$id"),
    filePath = "",
    creationTime = "",
    messageType = MessageExtraType.Image
  )

): Message {
  return Message(
    id = id,
    taskId = taskId,
    scheduleId = scheduleId,
    createdAt = Clock.System.now(),
    updatedAt = Clock.System.now(),
    contentType = contentType,
    content = content,
    deleted = false,
    openGraphResult,
    messageExtra
  )
}