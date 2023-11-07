package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import kotlinx.datetime.LocalDateTime

@Immutable
data class ScheduleUiModel(
    val localDateTime: LocalDateTime,
    val displayDate: String,
    val displayTime: String,
    val hour: Int = localDateTime.hour,
    val minute: Int = localDateTime.minute,
    val epochMs: Long = localDateTime.currentTimeZoneInstant.toEpochMilliseconds(),
)