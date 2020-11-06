package co.softov.morestuff.androidApp.domain.service

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: String)
    fun cancelSchedule(scheduleId: Long)

}