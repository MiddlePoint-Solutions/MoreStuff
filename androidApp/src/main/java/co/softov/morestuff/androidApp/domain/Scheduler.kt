package co.softov.morestuff.androidApp.domain

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: Long)
    fun cancelSchedule(scheduleId: Long)

}