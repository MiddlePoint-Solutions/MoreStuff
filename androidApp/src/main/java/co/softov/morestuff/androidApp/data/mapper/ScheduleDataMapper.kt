package co.softov.morestuff.androidApp.data.mapper

import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle

typealias ScheduleDbMapper = (co.softov.morestuff.db.Schedule) -> Schedule

typealias ScheduleWithTitleDbMapper = (
    id: Long,
    task_id: Long,
    schedule_time: String?,
    title: String
) -> ScheduleWithTitle

fun makeScheduleDbMapper(): ScheduleDbMapper = { schedule ->
    mapScheduleDb(schedule)
}

fun makeScheduleWithTitleDbMapper(): ScheduleWithTitleDbMapper = ::mapScheduleWithTitle

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

fun mapScheduleWithTitle(
    id: Long,
    task_id: Long,
    schedule_time: String?,
    title: String
): ScheduleWithTitle {
    return ScheduleWithTitle(
        scheduleId = id,
        taskId = task_id,
        taskTitle = title,
        scheduleTime = schedule_time
    )
}