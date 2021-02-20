package co.softov.morestuff.android.data.service

import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.LaterOption
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.TodayOption
import co.softov.morestuff.android.domain.enums.TomorrowOption
import co.softov.morestuff.android.domain.service.TimeManager
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimeManagerImpl(
    private val debug: DevTools
) : TimeManager {

    override fun getTodayTimeRange(): Pair<String, String> = TimeUtils.todayTimeStringPair

    override fun getPriorityTime(priority: Priority): String? {

        return when (val option = priority.value) {
            is TodayOption -> getTimeForToday(option)
            is TomorrowOption -> getTimeForTomorrow(option)
            is LaterOption -> null
            else -> null
        }
    }

    private fun getTimeForToday(option: TodayOption): String = when (option) {
        TodayOption.Automatic -> {
            val currentTime = when (debug.debugReminders) {
                true -> TimeUtils.todayLocalDateTimeByAdding(minute = debug.todayDebugTime)
                else -> TimeUtils.todayLocalDateTimeByAdding(hour = 1)
//                else -> TimeUtils.currentUtcInstant + 1.hours
            }
            currentTime.toString()
        }
    }

    private fun getTimeForTomorrow(option: TomorrowOption): String = when (option) {
        TomorrowOption.Automatic -> {
            TimeUtils.tomorrowLocalDateTime(hour = 9).toString()
        }
    }
}