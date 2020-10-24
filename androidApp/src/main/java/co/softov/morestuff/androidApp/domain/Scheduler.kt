package co.softov.morestuff.androidApp.domain

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: String)
    fun cancelSchedule(scheduleId: Long)

}