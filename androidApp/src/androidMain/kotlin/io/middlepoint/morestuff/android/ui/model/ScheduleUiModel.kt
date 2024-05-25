package io.middlepoint.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Immutable
data class ScheduleUiModel(
    val scheduleLocalDateTime: @RawValue LocalDateTime,
    val displayDate: String,
    val displayTime: String,
    val scheduleUtcTimeMillis: Long,
    val dayStartUtcTimeMillis: Long,
    val currentUtcTimeMillis: Long,
) : Parcelable {

    val hour: Int get() = scheduleLocalDateTime.hour
    val minute: Int get() = scheduleLocalDateTime.minute

    val isTimeInPast: Boolean
        get() = scheduleUtcTimeMillis <= currentUtcTimeMillis

}
