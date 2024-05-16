package io.middlepoint.morestuff.android.domain.model

import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleDomain(
    val id: Long = 0L,
    val taskId: Long = 0L,
    val createTime: String = "",
    val scheduleLocalTime: String? = null,
    val scheduleUtcTime: String? = null,
    val timezone: String = "",
    val active: Boolean = false,
    val scheduleType: ScheduleType,
) : Parcelable

fun ScheduleDomain.isReminder(): Boolean = scheduleType == ScheduleType.Reminder
fun ScheduleDomain.isOneTime(): Boolean = scheduleType == ScheduleType.OneTime
