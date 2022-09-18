package co.softov.morestuff.android.data.service

import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.*
import co.softov.morestuff.android.domain.service.TimeManager

class TimeManagerImpl(
    private val debug: DevTools
) : TimeManager {

    override fun getTodayTimeRange(): Pair<String, String> = TimeUtils.todayTimeStringPair

    override fun getPriorityTime(priority: Priority): String? = when (debug.debugReminders) {
        true -> TimeUtils.todayLocalDateTimeByAdding(minute = debug.todayDebugTime).toString()
        else -> when (val option = priority.option) {
            is TimeOfDayOption -> getTimeForOption(priority, option)
            is DefaultOption -> getDefaultOptionTime(priority, option)
            is LaterOption -> getLaterOptionTime(option)
        }
    }

    private fun getTimeForOption(priority: Priority, option: TimeOfDayOption): String? =
        when (option) {
            TimeOfDayOption.Morning -> timeForPriority(priority, 8)
            TimeOfDayOption.Noon -> timeForPriority(priority, 12)
            TimeOfDayOption.Afternoon -> timeForPriority(priority, 17)
            TimeOfDayOption.Evening -> timeForPriority(priority, 20)
        }


    private fun getDefaultOptionTime(priority: Priority, option: DefaultOption): String? =
        when (option) {
            DefaultOption.Custom, // TODO: implement custom
            DefaultOption.Auto -> when (priority) {
                is Priority.Later -> null
                is Priority.Today -> TimeUtils.todayLocalDateTimeByAdding(hour = 1).toString()
                is Priority.Tomorrow -> TimeUtils.tomorrowLocalDateTime(hour = 9).toString()
            }
        }

    private fun getLaterOptionTime(option: LaterOption): String? =
        when (option) {
            LaterOption.Weekend -> TimeUtils.weekendLocalDateTime().toString()
            LaterOption.Someday -> null
        }

    private fun timeForPriority(priority: Priority, hour: Int, minutes: Int = 0) =
        when (priority) {
            is Priority.Today -> TimeUtils.todayLocalDateTime(hour, minutes).toString()
            is Priority.Tomorrow -> TimeUtils.tomorrowLocalDateTime(hour, minutes).toString()
            is Priority.Later -> null
        }
}