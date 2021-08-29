package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle

typealias ScheduleData = co.softov.morestuff.db.Schedule
typealias ScheduleDbMapper = (ScheduleData) -> Schedule

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

fun mapScheduleDb(input: ScheduleData): Schedule {
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