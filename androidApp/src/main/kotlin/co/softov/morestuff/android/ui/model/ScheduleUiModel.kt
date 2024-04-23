package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.data.utils.toDayStartUtcTimeMillis
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
    val utcTimeMillis: Long = localDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds(),
    val dayStartUtcTimeMillis: Long,
)
