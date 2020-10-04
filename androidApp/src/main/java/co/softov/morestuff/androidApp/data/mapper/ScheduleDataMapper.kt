package co.softov.morestuff.androidApp.data.mapper

import co.softov.morestuff.db.SelectActiveSchedulesWithTaskTitle
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle

typealias ScheduleDbMapper = (co.softov.morestuff.db.Schedule) -> Schedule
typealias ScheduleWithTitleDbMapper = (SelectActiveSchedulesWithTaskTitle) -> ScheduleWithTitle

fun makeScheduleDbMapper(): ScheduleDbMapper = { schedule ->
    mapScheduleDb(schedule)
}

fun makeScheduleWithTitleDbMapper(): ScheduleWithTitleDbMapper = { schedule ->
    mapScheduleWithTitle(schedule)
}

fun mapScheduleDb(input: co.softov.morestuff.db.Schedule): Schedule {
    return Schedule(
        id = input.id,
        taskId = input.task_id,
        createTime = input.create_time,
        scheduleTime = input.schedule_time,
        type = input.type,
        fulfilled = input.fulfilled
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