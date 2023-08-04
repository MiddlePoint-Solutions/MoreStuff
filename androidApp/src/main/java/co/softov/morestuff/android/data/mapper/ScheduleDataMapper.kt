@file:Suppress("LocalVariableName")

package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleWithTitle

typealias ScheduleData = co.softov.morestuff.db.Schedule
typealias ScheduleDataMapper = (ScheduleData) -> ScheduleDomain
typealias ScheduleDomainMapper = (ScheduleDomain) -> ScheduleData

typealias ScheduleDbMapper = (
    id: Long,
    task_id: Long,
    create_time: String,
    schedule_time_local: String?,
    schedule_time_utc: String?,
    timezone: String,
    active: Boolean,
    schedule_type: ScheduleType
) -> ScheduleDomain

typealias ScheduleWithTitleDbMapper = (
    id: Long,
    task_id: Long,
    schedule_time: String?,
    title: String
) -> ScheduleWithTitle

fun makeScheduleDataMapper(): ScheduleDataMapper = { schedule ->
    mapScheduleData(schedule)
}

fun makeScheduleDbMapper(): ScheduleDbMapper = ::mapScheduleDb

fun makeScheduleDomainMapper(): ScheduleDomainMapper = { schedule ->
    mapScheduleDomain(schedule)
}

fun makeScheduleWithTitleDbMapper(): ScheduleWithTitleDbMapper = ::mapScheduleWithTitle

fun mapScheduleData(input: ScheduleData): ScheduleDomain {
    return ScheduleDomain(
        id = input.id,
        taskId = input.task_id,
        createTime = input.create_time,
        scheduleLocalTime = input.schedule_time_local,
        scheduleUtcTime = input.schedule_time_utc,
        timezone = input.timezone,
        active = input.active,
        scheduleType = input.schedule_type
    )
}

fun mapScheduleDb(
    id: Long,
    task_id: Long,
    create_time: String,
    schedule_time_local: String?,
    schedule_time_utc: String?,
    timezone: String,
    active: Boolean,
    schedule_type: ScheduleType
): ScheduleDomain {
    return ScheduleDomain(
        id = id,
        taskId = task_id,
        createTime = create_time,
        scheduleLocalTime = schedule_time_local,
        scheduleUtcTime = schedule_time_utc,
        timezone = timezone,
        active = active,
        scheduleType = schedule_type
    )
}

fun mapScheduleDomain(input: ScheduleDomain): ScheduleData {
    return with(input) {
        ScheduleData(
            id = input.id,
            task_id = taskId,
            create_time = createTime,
            schedule_time_local = scheduleLocalTime,
            schedule_time_utc = scheduleUtcTime,
            timezone = timezone,
            active = active,
            schedule_type = scheduleType
        )
    }
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