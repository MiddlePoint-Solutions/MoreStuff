@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.core.Schedule

typealias ScheduleDataMapper = (
    id: Long,
    uuid: String,
    task_id: Long,
    create_time: String,
    schedule_time_local: String?,
    schedule_time_utc: String?,
    timezone: String,
    active: Boolean,
    schedule_type: ScheduleType
) -> Schedule

fun makeScheduleDataMapper(): ScheduleDataMapper = ::mapScheduleData

fun mapScheduleData(
    id: Long,
    uuid: String,
    task_id: Long,
    create_time: String,
    schedule_time_local: String?,
    schedule_time_utc: String?,
    timezone: String,
    active: Boolean,
    schedule_type: ScheduleType
): Schedule = Schedule(
    id = id,
    taskId = task_id,
    createTime = create_time,
    scheduleLocalTime = schedule_time_local,
    scheduleUtcTime = schedule_time_utc,
    timezone = timezone,
    active = active,
    scheduleType = schedule_type
)
