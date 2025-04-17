package io.middlepoint.morestuff.shared.domain.service

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationAction
import platform.UserNotifications.UNNotificationCategory
import platform.UserNotifications.UNNotificationCategoryOptionNone
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait


class NotifierImpl(
  ) : Notifier, KoinComponent {
  private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
  private val logger = Logger.withTag("NotifierImpl")


  init {
    logger.i { "Initializing NotifierImpl and creating notification channels" }
    createNotificationChannels()
  }


  override fun showReminderNotification(message: Message) {
/*    logger.i { "Preparing to show reminder notification for scheduleId=${message.scheduleId}" }

    val notificationContent = createReminderNotificationContent(message)

    *//*val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
        timeInterval = 5.0,
        repeats = false
    ) *//*

    val request = UNNotificationRequest.requestWithIdentifier(
      identifier = message.scheduleId.toString(),
      content = notificationContent,
      trigger = null
    )

    logger.i { "Adding notification request for scheduleId=${message.scheduleId}" }
    notificationCenter.addNotificationRequest(request) { error ->
      if (error == null) {
        logger.i { "Successfully scheduled reminder notification for scheduleId=${message.scheduleId}" }
      } else {
        logger.e { "Error scheduling reminder notification: ${error.localizedDescription}" }
      }
    }*/
  }

  private fun scheduleNotificationAtTime(message: Message, scheduleTime: String) {
    logger.i { "Scheduling notification for scheduleId=${message.scheduleId} at $scheduleTime" }

    val localDateTime = LocalDateTime.parse(scheduleTime)
    val triggerDate = NSDateComponents().apply {
      setYear(localDateTime.year.toLong())
      setMonth(localDateTime.monthNumber.toLong())
      setDay(localDateTime.dayOfMonth.toLong())
      setHour(localDateTime.hour.toLong())
      setMinute(localDateTime.minute.toLong())
      setSecond(localDateTime.second.toLong())
    }

    val notificationContent = createReminderNotificationContent(message)

    val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
      dateComponents = triggerDate,
      repeats = false
    )

    val request = UNNotificationRequest.requestWithIdentifier(
      identifier = message.scheduleId.toString(),
      content = notificationContent,
      trigger = trigger
    )

    notificationCenter.addNotificationRequest(request) { error ->
      if (error == null) {
        logger.i { "Successfully scheduled notification for scheduleId=${message.scheduleId}" }
      } else {
        logger.e { "Error scheduling notification: ${error.localizedDescription}" }
      }
    }
  }


  override fun showReminderNotifications(messages: List<Message>) {
    logger.i { "Preparing to show ${messages.size} reminder notifications" }
    messages.forEach { message ->
      showReminderNotification(message)
    }

    if (messages.size > 1) {
      logger.i { "Creating summary notification for ${messages.size} reminders" }
      val summaryContent = createSummaryNotificationContent(messages.size)
      val summaryRequest = UNNotificationRequest.requestWithIdentifier(
        identifier = Notifier.GROUP_KEY_REMINDERS,
        content = summaryContent,
        trigger = null
      )

      notificationCenter.addNotificationRequest(summaryRequest) { error ->
        if (error == null) {
          logger.i { "Successfully scheduled summary notification for reminders" }
        } else {
          logger.e { "Error scheduling summary notification: ${error.localizedDescription}" }
        }
      }
    }
  }

  override fun showReminderNotificationReply(scheduleId: Long, messages: List<Message>) {
    logger.i { "Preparing to show reminder notification reply for scheduleId=$scheduleId" }
    val message = messages.lastOrNull() ?: run {
      logger.e { "No messages found to create reply notification for scheduleId=$scheduleId" }
      return
    }

    val notificationContent = createReminderNotificationContent(message)

    val request = UNNotificationRequest.requestWithIdentifier(
      identifier = scheduleId.toString(),
      content = notificationContent,
      trigger = null
    )

    logger.i { "Adding reply notification request for scheduleId=$scheduleId" }
    notificationCenter.addNotificationRequest(request) { error ->
      if (error == null) {
        logger.i { "Successfully scheduled reminder notification reply for scheduleId=$scheduleId" }
      } else {
        logger.e { "Error scheduling reminder notification reply: ${error.localizedDescription}" }
      }
    }
  }

  override fun clearScheduleNotification(scheduleId: Uuid) {
    logger.i { "Clearing notification for scheduleId=$scheduleId" }
    notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(scheduleId.toString()))
    notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(scheduleId.toString()))
    logger.i { "Notification for scheduleId=$scheduleId cleared" }
  }

  override fun showReviewNotification() {
    logger.i { "Preparing to show review notification" }
    val notificationContent = UNMutableNotificationContent().apply {
      setTitle("Review Tasks")
      setBody("Take a minute to sort priorities.")
      setCategoryIdentifier(Notifier.REVIEW_CHANNEL_ID)
    }

    val request = UNNotificationRequest.requestWithIdentifier(
      identifier = Notifier.REVIEW_NOTIFICATION_ID.toString(),
      content = notificationContent,
      trigger = null
    )

    logger.i { "Adding review notification request" }
    notificationCenter.addNotificationRequest(request) { error ->
      if (error == null) {
        logger.i { "Successfully scheduled review notification" }
      } else {
        logger.e { "Error scheduling review notification: ${error.localizedDescription}" }
      }
    }
  }

  override fun cancelReminderNotifications() {
    logger.i { "Cancelling all reminder notifications" }
    notificationCenter.removeAllPendingNotificationRequests()
    notificationCenter.removeAllDeliveredNotifications()
    logger.i { "All reminder notifications cancelled" }
  }

  override fun getActiveNotificationScheduleIds(): List<Long> {
    logger.i { "Fetching active notification schedule IDs" }
    val ids = mutableListOf<Long>()
    val semaphore = dispatch_semaphore_create(0)

    notificationCenter.getDeliveredNotificationsWithCompletionHandler { notifications ->
      (notifications as? List<UNNotification>)?.forEach { notification ->
        notification.request.identifier.toLongOrNull()?.let {
          logger.i { "Active notification found with scheduleId=$it" }
          ids.add(it)
        }
      }
      dispatch_semaphore_signal(semaphore)
    }
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER)
    logger.i { "Fetched ${ids.size} active notification schedule IDs" }
    return ids
  }

  private fun createReminderNotificationContent(message: Message): UNMutableNotificationContent {
    logger.i { "Creating reminder notification content for scheduleId=${message.scheduleId}" }
    return UNMutableNotificationContent().apply {
      setTitle("Reminder")
      setBody(message.content)
      setUserInfo(mapOf("scheduleId" to message.scheduleId))
    }
  }

  private fun createSummaryNotificationContent(count: Int): UNMutableNotificationContent {
    logger.i { "Creating summary notification content for $count reminders" }
    return UNMutableNotificationContent().apply {
      setTitle("Reminders Summary")
      setBody("$count pending reminders.")
      setCategoryIdentifier(Notifier.REMINDERS_CHANNEL_ID)
    }
  }

  private fun createNotificationChannels() {
    logger.i { "Creating notification channels" }
    val categories = listOf(
      UNNotificationCategory.categoryWithIdentifier(
        identifier = Notifier.REMINDERS_CHANNEL_ID,
        actions = emptyList<UNNotificationAction>(),
        intentIdentifiers = emptyList<String>(),
        options = UNNotificationCategoryOptionNone
      ),
      UNNotificationCategory.categoryWithIdentifier(
        identifier = Notifier.REVIEW_CHANNEL_ID,
        actions = emptyList<UNNotificationAction>(),
        intentIdentifiers = emptyList<String>(),
        options = UNNotificationCategoryOptionNone
      )
    )
    notificationCenter.setNotificationCategories(categories.toSet())
    logger.i { "Notification channels created" }
  }
}
