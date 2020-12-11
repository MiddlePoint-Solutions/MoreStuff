package co.softov.morestuff.androidApp.domain.service

import co.softov.morestuff.androidApp.domain.enums.Priority

interface TimeManager {

    fun getPriorityTime(priority: Priority): String?

    fun getTodayTimeRange(): Pair<String, String>

}