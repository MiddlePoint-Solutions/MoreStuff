package io.middlepoint.morestuff.shared.domain.service


import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.SyncAction
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

class SchedulerImpl(
  private val timeManager: TimeManager,
) : Scheduler, KoinComponent {
  private val logger = Logger.withTag("SchedulerImpl")
  private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
  private val store: AppStore by inject()

  init {
    notificationCenter.delegate()
  }

  override fun scheduleAtExact(
    scheduleId: Uuid,
    scheduleTime: String,
    taskTitle: String,
    taskId: Uuid
  ) {
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
/*    BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
      identifier = "io.middlepoint.morestuff",
      usingQueue = null,
      launchHandler = { task ->
        logger.d(" Tarea de sincronización ejecutándose en segundo plano")

        registerBackgroundTaskHandler()
        MainScope().launch {
          try {
            store.dispatchSuspend(SyncAction.SyncIntervalAction)
            task?.setTaskCompletedWithSuccess(true)
          } catch (e: Exception) {
            logger.d("Error al sincronizar datos: ${e.message}")
            task?.setTaskCompletedWithSuccess(false)
          }
        }
      }
    )*/
  }
  override fun dataSyncWorker() {
    BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
      identifier = "io.middlepoint.morestuff",
      usingQueue = null,
      launchHandler = { task ->
        logger.d(" Tarea de sincronización ejecutándose en segundo plano")

        registerBackgroundTaskHandler()
        MainScope().launch {
          try {
            store.dispatchSuspend(SyncAction.SyncIntervalAction)
            task?.setTaskCompletedWithSuccess(true)
          } catch (e: Exception) {
            logger.d("Error al sincronizar datos: ${e.message}")
            task?.setTaskCompletedWithSuccess(false)
          }
        }
      }
    )
  }

  @OptIn(ExperimentalForeignApi::class)
  fun registerBackgroundTaskHandler() {
    val request = BGAppRefreshTaskRequest("io.middlepoint.morestuff").apply {
      earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(15 * 60.0)
    }

    try {
      val success = BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
      if (success) {
        logger.d("Tarea de sincronización periódica registrada correctamente")
      } else {
        logger.d("Error al registrar la tarea de sincronización periódica")
      }
    } catch (e: Exception) {
      logger.d("Error al registrar la tarea: ${e.message}")
    }
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

