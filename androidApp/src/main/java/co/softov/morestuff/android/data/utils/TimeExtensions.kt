package co.softov.morestuff.android.data.utils

import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import kotlinx.datetime.*

val ScheduleDomain.scheduleLocalDateTime: LocalDateTime? get() = scheduleLocalTime?.toLocalDateTime()

val LocalDateTime.currentTimeZoneInstant: Instant get() = this.toInstant(TimeZone.currentSystemDefault())

val String.inEpochMilliseconds: Long get() = this.toLocalDateTime().currentTimeZoneInstant.toEpochMilliseconds()