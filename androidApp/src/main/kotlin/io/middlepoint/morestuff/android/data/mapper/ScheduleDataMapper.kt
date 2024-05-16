@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.android.data.mapper

import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.enums.ScheduleType

typealias ScheduleData = io.middlepoint.morestuff.db.Schedule
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

fun makeScheduleDbMapper(): ScheduleDbMapper = ::mapScheduleDb

fun makeScheduleDomainMapper(): ScheduleDomainMapper = { schedule ->
    mapScheduleDomain(schedule)
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