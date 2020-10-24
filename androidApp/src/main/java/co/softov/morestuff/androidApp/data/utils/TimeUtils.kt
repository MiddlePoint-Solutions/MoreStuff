package co.softov.morestuff.androidApp.data.utils

import kotlinx.datetime.*
import kotlin.time.ExperimentalTime
import kotlin.time.days

@OptIn(ExperimentalTime::class)
object TimeUtils {

    val currentUtcInstant: Instant get() = Clock.System.now()

    val currentLocalDateTime: LocalDateTime
        get() = currentUtcInstant.toLocalDateTime(currentTimeZone)

    val currentTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

    val currentLocalDateTimeString: String get() = currentLocalDateTime.toString()

    fun tomorrowLocalDateTime(hour: Int, minute: Int = 0): LocalDateTime =
        (currentUtcInstant + 1.days).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

}


