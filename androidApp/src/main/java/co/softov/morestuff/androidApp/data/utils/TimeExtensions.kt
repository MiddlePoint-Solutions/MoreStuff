package co.softov.morestuff.androidApp.data.utils

import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.Task
import kotlinx.datetime.*

// Task
val Task.createLocalDateTime: LocalDateTime get() = createTime.toLocalDateTime()
val Task.completeLocalDateTime: LocalDateTime? get() = completeTime?.toLocalDateTime()

// Schedule
val Schedule.createLocalDateTime: LocalDateTime get() = createTime.toLocalDateTime()
val Schedule.createTimeInstant: Instant get() = createTime.toInstant()

val Schedule.scheduleLocalDateTime: LocalDateTime? get() = scheduleTime?.toLocalDateTime()
val Schedule.scheduleTimeInstant: Instant?
    get() = scheduleTime?.toLocalDateTime()?.toInstant(TimeZone.of(timezone))

// LocalDateTime
val LocalDateTime.currentTimeZoneInstant: Instant get() = this.toInstant(TimeZone.currentSystemDefault())
fun LocalDateTime.asCurrentTimeIn(tz: TimeZone): Instant = this.toInstant(tz)

// String
val String.toEpochMilliseconds: Long get() = this.toLocalDateTime().currentTimeZoneInstant.toEpochMilliseconds()