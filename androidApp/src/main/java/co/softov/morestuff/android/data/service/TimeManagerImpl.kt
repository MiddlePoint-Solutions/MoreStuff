package co.softov.morestuff.android.data.service

import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.DefaultOption
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.TimeOfDayOption
import co.softov.morestuff.android.domain.service.TimeManager

class TimeManagerImpl(
    private val debug: DevTools
) : TimeManager {

    override fun getTodayTimeRange(): Pair<String, String> = TimeUtils.todayTimeStringPair

    override fun getPriorityTime(priority: Priority): String? {

        return when (priority) {
            is Priority.Today -> getTimeForToday(priority.option)
            is Priority.Tomorrow -> getTimeForTomorrow(priority.option)
            else -> null
        }
    }

    private fun getTimeForToday(option: PriorityOption): String = when (option) {
        DefaultOption.Auto -> {
            val currentTime = when (debug.debugReminders) {
                true -> TimeUtils.todayLocalDateTimeByAdding(minute = debug.todayDebugTime)
                else -> TimeUtils.todayLocalDateTimeByAdding(hour = 1)
//                else -> TimeUtils.currentUtcInstant + 1.hours
            }
            currentTime.toString()
        }
        else -> "TODO"
    }

    private fun getTimeForTomorrow(option: PriorityOption): String = when (option) {
        DefaultOption.Auto -> {
            TimeUtils.tomorrowLocalDateTime(hour = 9).toString()
        }
        else -> "TODO"
    }
}