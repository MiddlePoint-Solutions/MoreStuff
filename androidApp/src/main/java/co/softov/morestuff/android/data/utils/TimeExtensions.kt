package co.softov.morestuff.android.data.utils

import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import kotlinx.datetime.*

// Task
val TaskDomain.createLocalDateTime: LocalDateTime get() = createTime.toLocalDateTime()
val TaskDomain.completeLocalDateTime: LocalDateTime? get() = completeTime?.toLocalDateTime()

// Schedule
val ScheduleDomain.createLocalDateTime: LocalDateTime get() = createTime.toLocalDateTime()
val ScheduleDomain.createTimeInstant: Instant get() = createTime.toInstant()

val ScheduleDomain.scheduleLocalDateTime: LocalDateTime? get() = scheduleLocalTime?.toLocalDateTime()
val ScheduleDomain.scheduleTimeInstant: Instant?
    get() = scheduleLocalTime?.toLocalDateTime()?.toInstant(TimeZone.of(timezone))

// LocalDateTime
val LocalDateTime.currentTimeZoneInstant: Instant get() = this.toInstant(TimeZone.currentSystemDefault())
fun LocalDateTime.asCurrentTimeIn(tz: TimeZone): Instant = this.toInstant(tz)

// String
val String.inEpochMilliseconds: Long get() = this.toLocalDateTime().currentTimeZoneInstant.toEpochMilliseconds()