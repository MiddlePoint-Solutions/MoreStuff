package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Immutable
data class ScheduleUiModel(
    val localDateTime: LocalDateTime,
    val displayDate: String,
    val displayTime: String,
    val hour: Int = localDateTime.hour,
    val minute: Int = localDateTime.minute,
    val epochMsUtc: Long = localDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds(),
)