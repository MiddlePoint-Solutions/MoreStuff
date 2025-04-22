package io.middlepoint.morestuff.shared.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.shared.MainActivity
import io.middlepoint.morestuff.shared.app.extensions.isAtLeastVersion
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import io.middlepoint.morestuff.shared.app.receiver.createReplyIntent
import io.middlepoint.morestuff.shared.app.receiver.random
import io.middlepoint.morestuff.shared.app.receiver.randomRequestCode
import io.middlepoint.morestuff.shared.data.utils.inEpochMilliseconds
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.DONE
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.LATER
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.SNOOZE
import io.middlepoint.morestuff.shared.domain.enums.ReplyType.TOMORROW
import io.middlepoint.morestuff.shared.domain.model.Defaults
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.Notifier.Companion.GROUP_KEY_REMINDERS
import io.middlepoint.morestuff.shared.domain.service.Notifier.Companion.REMINDERS_CHANNEL_ID
import io.middlepoint.morestuff.shared.domain.service.Notifier.Companion.REVIEW_CHANNEL_ID
import io.middlepoint.morestuff.shared.domain.service.Notifier.Companion.REVIEW_NOTIFICATION_ID
import org.koin.core.component.KoinComponent
import java.util.Calendar
import kotlin.random.Random

class NotifierImpl(
  private val context: Context,
  private val notificationManager: NotificationManagerCompat,
  private val settings: Settings,
  private val logger: Logger
) : Notifier, KoinComponent {

  private val activeNotifications: Array<StatusBarNotification>
    get() = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
      .activeNotifications

  private val appPerson: Person
  private val userPerson: Person

  private val random = Random(88888)

  private var notificationMap: Map<String, Int>
    get() = settings.getString(SCHEDULE_MAP_KEY, "")
      .let {
        when {
          it.isEmpty() -> mapOf()
          it.contains(SCHEDULE_MAP_SEPARATOR) -> {
            it.split(SCHEDULE_MAP_SEPARATOR)
              .also { logger.e { "$it" } }
              .associate { scheduleSplit ->
                scheduleSplit.split("=")
                  .let { schedulePair ->
                    logger.e { "$schedulePair" }
                    schedulePair[0] to schedulePair[1].toInt()
                  }
              }
          }
          else -> {
            buildMap {
              it.split("=").let { schedulePair ->
                logger.e { "$schedulePair" }
                put(schedulePair[0], schedulePair[1].toInt())
              }
            }
          }
        }
      }
    set(value) {
      logger.d { "Set: $value" }
      val mapString = buildString {
        value.toList().let { values ->
          values.forEachIndexed { index, pair ->
            append("${pair.first}=${pair.second}")
            if (values.lastIndex != index) {
              append(SCHEDULE_MAP_SEPARATOR)
            }
          }
        }
      }
      settings.putString(SCHEDULE_MAP_KEY, mapString)
    }

  companion object {
    private const val SUMMARY_ID = 99999
    private const val SCHEDULE_MAP_KEY = "SCHEDULE_MAP_KEY"
    private const val SCHEDULE_MAP_SEPARATOR = ";"
  }

  init {
    createNotificationChannels()
    appPerson = Person.Builder().setBot(true).setImportant(true).setName("Mr.Stuff").build()
    userPerson = Person.Builder().setName("Me").build()
  }

  override fun showReminderNotification(message: Message) {
    message.scheduleId?.let { scheduleId ->

      val notificationId = random.nextInt()
      val mutableNotificationMap = notificationMap.toMutableMap()
      mutableNotificationMap[scheduleId.value] = notificationId
      notificationMap = mutableNotificationMap

      notifyUser(
        notificationId,
        createReminderNotification(scheduleId, message.taskId, message.content)
      )
      val notificationCount = activeNotifications.size
      if (notificationCount > Defaults.REMINDER_GROUP_LIMIT) {
        val summaryNotification = createReviewSummaryNotification(notificationCount)
        notifyUser(
          notificationId,
          createReminderNotification(scheduleId, message.taskId, message.content)
        )
        notifyUser(SUMMARY_ID, summaryNotification)
      }
    }
  }

  private fun createReminderNotification(
    scheduleId: Uuid,
    taskId: Uuid,
    content: String
  ): Notification {

    val tomorrow = createReplyIntentWithTitle(scheduleId, TOMORROW)
    val snooze = createReplyIntentWithTitle(scheduleId, SNOOZE)
    val done = createReplyIntentWithTitle(scheduleId, DONE)

    val style = NotificationCompat.MessagingStyle(appPerson)
      .addMessage(content, Calendar.getInstance().timeInMillis, appPerson)

    val builder = NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_chat_24dp)
      .setOnlyAlertOnce(true)
      .setGroup(GROUP_KEY_REMINDERS)
      .setContentIntent(createReminderContentIntent(taskId))
      .setStyle(style)
      .addAction(R.drawable.ic_send_24dp, snooze.first, snooze.second)
      .addAction(R.drawable.ic_send_24dp, tomorrow.first, tomorrow.second)
      .addAction(R.drawable.ic_send_24dp, done.first, done.second)

    return builder.build()
  }

  private fun createReviewSummaryNotification(notificationCount: Int) =
    NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
      .setContentTitle("Review tasks")
      // Set content text to support devices running API level < 24.
      .setContentText("$notificationCount pending reminders")
      .setSmallIcon(R.drawable.ic_chat_24dp)
      // Build summary info into InboxStyle template.
      .setGroupSummary(true)
      // Specify which group this notification belongs to.
      .setGroup(GROUP_KEY_REMINDERS)
      // Set this notification as the summary for the group.
      .setContentIntent(
        createReviewContentIntent()
      ).build()

  private fun notifyUser(
    id: Int, notification: Notification
  ) {
    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      notificationManager.notify(id, notification)
    }
  }

  private fun createReplyIntentWithTitle(
    scheduleId: Uuid,
    type: ReplyType
  ): Pair<String, PendingIntent> {
    val actionText = when (type) {
      SNOOZE -> context.getString(R.string.schedule_reply_snooze)
      TOMORROW -> context.getString(R.string.schedule_reply_tomorrow)
      LATER -> context.getString(R.string.schedule_reply_later)
      DONE -> context.getString(R.string.schedule_reply_done)
    }
    return actionText to NotificationReceiver.createReplyIntent(context, scheduleId, type)
  }

  private fun createReminderContentIntent(
    taskId: Uuid
  ): PendingIntent =
    Intent(context, MainActivity::class.java).apply {
      action = ACTION_NOTIFICATION_REMINDER
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      putExtra(MainActivity.EXTRA_TASK_ID, taskId.value)
    }.let {
      val requestCode = randomRequestCode
      Logger.d("createContentIntent, requestCode: $requestCode")
      // Use random request code for replying to reminder intent.
      PendingIntent.getActivity(context, requestCode, it, PendingIntent.FLAG_IMMUTABLE)
    }

  private fun createReviewContentIntent(): PendingIntent =
    Intent(context, MainActivity::class.java).apply {
      action = ACTION_NOTIFICATION_REVIEW
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }.let {
      PendingIntent.getActivity(context, randomRequestCode, it, PendingIntent.FLAG_IMMUTABLE)
    }

  private fun addMessage(
    style: NotificationCompat.MessagingStyle,
    message: Message,
    time: Long
  ) {
    style.addMessage(
      message.content, time, getMessagePerson(message.contentType)
    )
  }

  private fun getMessagePerson(messageType: ContentType): Person {
    return when (messageType) {
      ContentType.CONFIRM_NEW_TASK,
      ContentType.TASK_REMINDER -> appPerson

      else -> userPerson
    }
  }

  override fun clearScheduleNotification(scheduleId: Uuid) {
    val mutableNotificationMap = notificationMap.toMutableMap()
    val notificationId = mutableNotificationMap[scheduleId.value]
    notificationId?.let {
      notificationManager.cancel(it)
      mutableNotificationMap.remove(scheduleId.value)
      notificationMap = mutableNotificationMap
    }
  }

  override fun showReviewNotification() {
    // TODO: use time to customize review message?
    val (title, message) = "Review tasks" to "Take a minute to sort priorities"

    val builder = NotificationCompat.Builder(context, REVIEW_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_notification)
      .setAutoCancel(true)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_REMINDER)
      .setContentIntent(createReviewContentIntent())

    notifyUser(REVIEW_NOTIFICATION_ID, builder.build())
  }

  override fun cancelReminderNotifications() {
    notificationManager.cancelAll()
  }

  override fun getActiveNotificationScheduleIds(): List<Long> =
    activeNotifications.map { it.id.toLong() }

  private fun createNotificationChannels() {
    if (isAtLeastVersion(Build.VERSION_CODES.O)) {
      NotificationChannel(
        REMINDERS_CHANNEL_ID,
        "Reminder Notifications",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notifications for your reminders"
      }.also {
        notificationManager.createNotificationChannel(it)
      }

      NotificationChannel(
        REVIEW_CHANNEL_ID,
        "Review Notifications",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notifications for reviewing your tasks"
      }.also {
        notificationManager.createNotificationChannel(it)
      }
    }
  }
}