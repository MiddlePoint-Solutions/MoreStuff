package co.softov.morestuff.android.domain

import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.data.utils.TimeUtils.nowLocalDateTimeString
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.*
import java.time.LocalDateTime


fun createListOfTasks(amount: Long): List<Task> {
    return buildList {
        for (i in 1..amount) {
            add(createTaskForTest())
        }
    }
}

fun createTaskForTest(id: Long = 1, title: String = "", timeUtils: String = nowLocalDateTimeString): Task {
    return Task(id, title, timeUtils)
}


fun createListOfSchedulesForTest( amount: Long):List<Schedule>{
    return  buildList {
        for(i in 1..amount){
            add(createScheduleForTest())
        }
    }
}

fun createScheduleForTest(
    id: Long = 1,
    taskId: Long = 1,
    createTime: String = "",
    scheduleTimeLocal: String = TimeUtils.getCreateTime(),
    scheduleTimeUtc: String? = null,
    timeZone: String = TimeUtils.currentTimeZone.id,
    active: Boolean = true,
): Schedule {
    return Schedule(id, taskId, createTime, scheduleTimeLocal,scheduleTimeUtc, timeZone, active)
}

fun createScheduleWithTitleList ( amount: Long):List<ScheduleWithTitle>{
    return  buildList {
        for(i in 1..amount){
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
    return ScheduleWithTitle(scheduleId,taskId,scheduleTime,taskTitle)
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

fun expectedTodayOptionsSize(): Int {
    val now = LocalDateTime.now()
    val morning = now.toLocalDate().atTime(8, 0)
    val noon = now.toLocalDate().atTime(12, 0)
    val afternoon = now.toLocalDate().atTime(18, 0)
    val timeOptions = TimeOfDayOption.values().size

    return when {
        now < morning -> timeOptions + 2
        now < noon -> timeOptions - 1 + 2
        now < afternoon -> timeOptions - 2 + 2
        else -> 2
    }
}