package co.softov.morestuff.android.app.service

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
import co.softov.morestuff.android.ui.MainActivity
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.extensions.isAtLeastVersion
import co.softov.morestuff.android.app.receiver.NotificationReceiver
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import co.softov.morestuff.android.app.receiver.createReplyIntent
import co.softov.morestuff.android.app.receiver.randomRequestCode
import co.softov.morestuff.android.data.utils.inEpochMilliseconds
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ReplyType.*
import co.softov.morestuff.android.domain.model.Defaults
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.service.Notifier.Companion.GROUP_KEY_REMINDERS
import co.softov.morestuff.android.domain.service.Notifier.Companion.REMINDERS_CHANNEL_ID
import co.softov.morestuff.android.domain.service.Notifier.Companion.REVIEW_CHANNEL_ID
import co.softov.morestuff.android.domain.service.Notifier.Companion.REVIEW_NOTIFICATION_ID
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.util.*

class NotifierImpl(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) : Notifier, KoinComponent {

    private val activeNotifications: Array<StatusBarNotification>
        get() = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .activeNotifications

    private val appPerson: Person
    private val userPerson: Person

    companion object {
        private const val SUMMARY_ID = 99999
    }


    init {
        createNotificationChannels()
        appPerson = Person.Builder().setBot(true).setImportant(true).setName("Mr.Stuff").build()
        userPerson = Person.Builder().setName("Me").build()
    }

    override fun showReminderNotification(message: Message) {
        notifyUser(message.scheduleId.toInt(), createReminderNotification(message))
        val notificationCount = activeNotifications.size
        if (notificationCount > Defaults.REMINDER_GROUP_LIMIT) {
            val summaryNotification = createReviewSummaryNotification(notificationCount)

            notifyUser(message.scheduleId.toInt(), createReminderNotification(message))
            notifyUser(SUMMARY_ID, summaryNotification)
        }
    }

    override fun showReminderNotifications(messages: List<Message>) {
        val summaryNotification = createReviewSummaryNotification(messages.size)
        val message = messages.last()
        notifyUser(message.scheduleId.toInt(), createReminderNotification(message))
        notifyUser(SUMMARY_ID, summaryNotification)
    }

    private fun createReminderNotification(message: Message): Notification {
        val scheduleId = message.scheduleId
        val tomorrow = createReplyIntentWithTitle(scheduleId, TOMORROW)
        val snooze = createReplyIntentWithTitle(scheduleId, SNOOZE)
        val done = createReplyIntentWithTitle(scheduleId, DONE)

        val style = NotificationCompat.MessagingStyle(appPerson)
            .addMessage(message.content, Calendar.getInstance().timeInMillis, appPerson)

        val builder = NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_chat_24dp)
            .setOnlyAlertOnce(true)
            .setGroup(GROUP_KEY_REMINDERS)
            .setContentIntent(createReminderContentIntent(message.taskId))
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
        scheduleId: Long,
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
        taskId: Long
    ): PendingIntent =
        Intent(context, MainActivity::class.java).apply {
            action = ACTION_NOTIFICATION_REMINDER
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_TASK_ID, taskId)
        }.let {
            val requestCode = randomRequestCode
            Timber.d("createContentIntent, requestCode: $requestCode")
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

    @SuppressLint("MissingPermission")
    override fun showReminderNotificationReply(
        scheduleId: Long,
        messages: List<Message>
    ) {
        val messageStyle = getMessagingStyle(scheduleId.toInt(), messages)
        val notification: Notification =
            NotificationCompat.Builder(context, REMINDERS_CHANNEL_ID)
                .setStyle(messageStyle)
                .setSmallIcon(R.drawable.ic_chat_24dp)
                .setOnlyAlertOnce(true)
                .setTimeoutAfter(2000L)
                .build()

        notifyUser(scheduleId.toInt(), notification)
    }

    private fun getMessagingStyle(
        notificationId: Int,
        messages: List<Message>
    ): NotificationCompat.MessagingStyle {

        var style = getActiveNotificationById(notificationId)?.let {
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(it)
        }

        if (style == null) {
            style = NotificationCompat.MessagingStyle(appPerson)
            for (message in messages) {
                val time = message.createTime.inEpochMilliseconds
                addMessage(style, message, time)
            }
        } else {
            val message = messages.last()
            val time = message.createTime.inEpochMilliseconds
            addMessage(style, message, time)
        }

        return style
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

    private fun getActiveNotificationById(id: Int): Notification? =
        when {
            isAtLeastVersion(Build.VERSION_CODES.M) -> {
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .activeNotifications.firstOrNull { it.id == id }?.notification
            }

            else -> null
        }

    override fun userInteractedWithNotification(scheduleId: Long) {
        notificationManager.cancel(scheduleId.toInt())
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