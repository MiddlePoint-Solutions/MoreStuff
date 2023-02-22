package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.Priority

interface TimeManager {

    fun getPriorityTime(priority: Priority): String?

    fun getTodayTimeRange(): Pair<String, String>

}