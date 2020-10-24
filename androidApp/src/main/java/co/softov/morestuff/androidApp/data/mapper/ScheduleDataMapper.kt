package co.softov.morestuff.androidApp.data.mapper

import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.db.SelectActiveLaterSchedulesWithTaskTitle
import co.softov.morestuff.db.SelectActiveSchedulesWithTaskTitle
import co.softov.morestuff.db.SelectActiveTodaySchedulesWithTaskTitle
import co.softov.morestuff.db.SelectTomorrowSchedulesWithTaskTitle

typealias ScheduleDbMapper = (co.softov.morestuff.db.Schedule) -> Schedule
typealias ScheduleWithTitleDbMapper<T> = (T) -> ScheduleWithTitle

fun makeScheduleDbMapper(): ScheduleDbMapper = { schedule ->
    mapScheduleDb(schedule)
}

fun <T> makeScheduleWithTitleDbMapper(): ScheduleWithTitleDbMapper<T> = { schedule ->
    when (schedule) {
        is SelectActiveSchedulesWithTaskTitle -> mapScheduleWithTitle(schedule)
        is SelectActiveLaterSchedulesWithTaskTitle -> mapScheduleWithTitle(schedule)
        is SelectActiveTodaySchedulesWithTaskTitle -> mapScheduleWithTitle(schedule)
        is SelectTomorrowSchedulesWithTaskTitle -> mapScheduleWithTitle(schedule)
        else -> throw IllegalArgumentException("Mapper $schedule does not exist")
    }
}

fun mapScheduleDb(input: co.softov.morestuff.db.Schedule): Schedule {
    return Schedule(
        id = input.id,
        taskId = input.task_id,
        createTime = input.create_time,
        scheduleTime = input.schedule_time,
        timezone = input.timezone,
        active = input.active
    )
}

fun mapScheduleWithTitle(input: SelectActiveSchedulesWithTaskTitle): ScheduleWithTitle {
    return ScheduleWithTitle(
        scheduleId = input.id,
        taskId = input.task_id,
        taskTitle = input.title,
        scheduleTime = input.schedule_time
    )
}

fun mapScheduleWithTitle(input: SelectActiveLaterSchedulesWithTaskTitle): ScheduleWithTitle {
    return ScheduleWithTitle(
        scheduleId = input.id,
        taskId = input.task_id,
        taskTitle = input.title,
        scheduleTime = input.schedule_time
    )
}

fun mapScheduleWithTitle(input: SelectActiveTodaySchedulesWithTaskTitle): ScheduleWithTitle {
    return ScheduleWithTitle(
        scheduleId = input.id,
        taskId = input.task_id,
        taskTitle = input.title,
        scheduleTime = input.schedule_time
    )
}

fun mapScheduleWithTitle(input: SelectTomorrowSchedulesWithTaskTitle): ScheduleWithTitle {
    return ScheduleWithTitle(
        scheduleId = input.id,
        taskId = input.task_id,
        taskTitle = input.title,
        scheduleTime = input.schedule_time
    )
}