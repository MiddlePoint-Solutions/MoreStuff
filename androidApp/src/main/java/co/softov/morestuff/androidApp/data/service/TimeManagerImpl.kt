package co.softov.morestuff.androidApp.data.service

import co.softov.morestuff.androidApp.data.utils.TimeUtils
import co.softov.morestuff.androidApp.domain.Debug
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.TimeOption
import co.softov.morestuff.androidApp.domain.service.TimeManager
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimeManagerImpl(
    private val debug: Debug
) : TimeManager {

    override fun getTodayTimeRange(): Pair<String, String> = TimeUtils.todayTimeStringPair

    override fun getPriorityTime(priority: Priority): String? {
        return when (priority) {
            is Priority.Today -> getTimeForToday(priority.option)
            is Priority.Tomorrow -> getTimeForTomorrow(priority.option)
            is Priority.Later -> null
        }
    }

    private fun getTimeForToday(option: TimeOption): String = when (option) {
        TimeOption.Default -> {
            val currentTime = when (debug.debugReminders) {
                true -> TimeUtils.todayLocalDateTimeByAdding(minute = debug.todayDebugTime)
                else -> TimeUtils.todayLocalDateTimeByAdding(hour = 1)
//                else -> TimeUtils.currentUtcInstant + 1.hours
            }
            currentTime.toString()
        }
    }

    private fun getTimeForTomorrow(option: TimeOption): String = when (option) {
        TimeOption.Default -> {
            TimeUtils.tomorrowLocalDateTime(hour = 9).toString()
        }
    }
}