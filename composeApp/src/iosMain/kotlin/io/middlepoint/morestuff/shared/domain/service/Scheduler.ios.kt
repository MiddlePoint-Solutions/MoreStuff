package io.middlepoint.morestuff.shared.domain.service


import co.touchlab.kermit.Logger
import kotlinx.datetime.LocalDateTime
import platform.Foundation.*
import platform.UserNotifications.*
import kotlin.time.Duration.Companion.minutes

class SchedulerImpl(
    private val timeManager: TimeManager
) : Scheduler {
   private val logger = Logger.withTag("SchedulerImpl")

 /*   override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
        val triggerDate = NSDateComponents().apply {
            val localDateTime = LocalDateTime.parse(scheduleTime)
            year = localDateTime.year.toLong()
            month = localDateTime.monthNumber.toLong()
            day = localDateTime.dayOfMonth.toLong()
            hour = localDateTime.hour.toLong()
            minute = localDateTime.minute.toLong()
            second = localDateTime.second.toLong()
        }

        val content = UNMutableNotificationContent()
        content.setTitle("Scheduled Task")
        content.setBody("Task with ID $scheduleId is scheduled.")


        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = triggerDate,
            repeats = false
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = getScheduleWorkTag(scheduleId),
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                error?.let {
                    println("Error scheduling notification: ${it.localizedDescription}")
                }
            }
    }*/

    override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
        logger.i { "Scheduling task with scheduleId=$scheduleId at $scheduleTime" }

        val triggerDate = NSDateComponents().apply {
            val localDateTime = LocalDateTime.parse(scheduleTime)
            setYear(localDateTime.year.toLong())
            setMonth(localDateTime.monthNumber.toLong())
            setDay(localDateTime.dayOfMonth.toLong())
            setHour(localDateTime.hour.toLong())
            setMinute(localDateTime.minute.toLong())
            setSecond(localDateTime.second.toLong())
        }

        val content = UNMutableNotificationContent()
        content.setTitle("Scheduled Task")
        content.setBody("Task with ID $scheduleId is scheduled.")
        content.setUserInfo(mapOf("scheduleId" to scheduleId))

        logger.i { "Created notification content for scheduleId=$scheduleId" }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = triggerDate,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = getScheduleWorkTag(scheduleId),
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                if (error == null) {
                    logger.i { "Successfully scheduled task with scheduleId=$scheduleId" }
                } else {
                    logger.e { "Error scheduling task: ${error.localizedDescription}" }
                }
            }
    }


    override fun schedulePlannedPriorityWorker() {
        val content = UNMutableNotificationContent()
        content.setTitle("Planned Priority Update")
        content.setBody("Executing planned priority update.")


        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 20.minutes.inWholeSeconds.toDouble(), // Asegúrate de usar Double
            repeats = true
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = PLANNED_PRIORITY_WORK,
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                error?.let {
                    println("Error scheduling planned priority worker: ${it.localizedDescription}")
                }
            }
    }

    override fun scheduleReviewWorker(hour: Int, minute: Int) {
        val currentTime = timeManager.nowLocalDateTime
        val scheduleTime =
            if (currentTime.hour > hour || (currentTime.hour == hour && currentTime.minute >= minute)) {
                timeManager.tomorrowLocalDateTime(hour, minute)
            } else {
                timeManager.todayLocalDateTime(hour, minute)
            }

        val triggerDate = NSDateComponents().apply {
            setYear(scheduleTime.year.toLong())
            setMonth(scheduleTime.monthNumber.toLong())
            setDay(scheduleTime.dayOfMonth.toLong())
            setHour(scheduleTime.hour.toLong())
            setMinute(scheduleTime.minute.toLong())
        }


        val content = UNMutableNotificationContent()
        content.setTitle("Review Reminder")
        content.setBody("It's time for your scheduled review.")


        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = triggerDate,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = PRIORITY_REVIEW_WORK,
            content = content,
            trigger = trigger
        )


        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                error?.let {
                    println("Error scheduling review worker: ${it.localizedDescription}")
                }
            }
    }

    override fun scheduleNextReview(hour: Int, minute: Int, replaceExisting: Boolean) {
        if (replaceExisting) {
            cancelPlannedPriorityUpdate()
        }

        scheduleReviewWorker(hour, minute)
    }

    override fun cancelSchedule(scheduleId: Long) {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(
                listOf(getScheduleWorkTag(scheduleId))
            )
    }

    override fun cancelPlannedPriorityUpdate() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(
                listOf(PLANNED_PRIORITY_WORK)
            )
    }

    private fun getScheduleWorkTag(scheduleId: Long) = "SCHEDULE_$scheduleId"

    companion object {
        private const val PLANNED_PRIORITY_WORK = "SmartReminder"
        private const val PRIORITY_REVIEW_WORK = "PriorityReview"
    }
}
