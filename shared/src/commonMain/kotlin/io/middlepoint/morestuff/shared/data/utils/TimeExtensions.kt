package io.middlepoint.morestuff.shared.data.utils

import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import kotlinx.datetime.*

val ScheduleDomain.scheduleLocalDateTime: LocalDateTime? get() = scheduleLocalTime?.toLocalDateTime()

val LocalDateTime.currentTimeZoneInstant: Instant get() = this.toInstant(TimeZone.currentSystemDefault())

val String.inEpochMilliseconds: Long get() = this.toLocalDateTime().currentTimeZoneInstant.toEpochMilliseconds()

fun LocalDateTime.toDayStartUtcTimeMillis(): Long =
    LocalDateTime(year, month, dayOfMonth, 0, 0, 0, 0)
        .toInstant(TimeZone.UTC)
        .toEpochMilliseconds()