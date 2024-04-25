package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Immutable
data class ScheduleUiModel(
    val scheduleLocalDateTime: LocalDateTime,
    val displayDate: String,
    val displayTime: String,
    val scheduleUtcTimeMillis: Long,
    val dayStartUtcTimeMillis: Long,
    val currentUtcTimeMillis: Long,
) {

    val hour: Int get() = scheduleLocalDateTime.hour
    val minute: Int get() = scheduleLocalDateTime.minute

    val isTimeInPast: Boolean
        get() = scheduleUtcTimeMillis <= currentUtcTimeMillis

}
