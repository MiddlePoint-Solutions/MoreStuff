package io.middlepoint.morestuff.shared.domain.service


import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SchedulerImpl(
  private val timeManager: TimeManager,
) : Scheduler {
   private val logger = Logger.withTag("SchedulerImpl")
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()


    init {
        notificationCenter.delegate()
    }

    override fun scheduleAtExact(scheduleId: Uuid, scheduleTime: String, taskTitle: String, taskId: Uuid) {
        logger.i { "Scheduling notification with scheduleId=$scheduleId at $scheduleTime" }

      val localDateTime = Instant.parse(scheduleTime).toLocalDateTime(TimeZone.currentSystemDefault())

        val triggerDate = NSDateComponents().apply {
            setYear(localDateTime.year.toLong())
            setMonth(localDateTime.monthNumber.toLong())
            setDay(localDateTime.dayOfMonth.toLong())
            setHour(localDateTime.hour.toLong())
            setMinute(localDateTime.minute.toLong())
            setSecond(localDateTime.second.toLong())
        }

        val content = UNMutableNotificationContent().apply {
            setTitle("Reminder")
            setBody(taskTitle)
            setUserInfo(mapOf("scheduleId" to scheduleId.value, "taskId" to taskId.value))
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = triggerDate,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = getScheduleWorkTag(scheduleId),
            content = content,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(request) { error ->
            if (error == null) {

                logger.i { "Successfully scheduled notification for scheduleId=$scheduleId" }
            } else {
                logger.e { "Error scheduling notification: ${'$'}{error.localizedDescription}" }
            }
        }

    }


    override fun scheduleDataSyncWorker() {
    /*    val content = UNMutableNotificationContent()
        content.setTitle("Planned Priority Update")
        content.setBody("Executing planned priority update.")


        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.days.inWholeSeconds.toDouble(),
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
            }*/
    }



  override fun cancelSchedule(scheduleId: Uuid) {
    UNUserNotificationCenter.currentNotificationCenter()
      .removePendingNotificationRequestsWithIdentifiers(
        listOf(getScheduleWorkTag(scheduleId))
      )
    logger.i { "Cancelled notification with scheduleId= $scheduleId" }
  }

  /*    override fun scheduleReviewWorker(hour: Int, minute: Int) {
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
      }*/

    override fun cancelPlannedPriorityUpdate() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(
                listOf(PLANNED_PRIORITY_WORK)
            )
    }

  private fun getScheduleWorkTag(scheduleId: Uuid) = "SCHEDULE_$scheduleId"

    companion object {
        private const val PLANNED_PRIORITY_WORK = "SmartReminder"
        private const val PRIORITY_REVIEW_WORK = "PriorityReview"
    }
}

