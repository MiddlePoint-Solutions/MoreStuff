package co.softov.morestuff.android.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import co.softov.morestuff.android.MainActivity
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.extensions.isAtLeastVersion
import co.softov.morestuff.android.app.receiver.NotificationReceiver
import co.softov.morestuff.android.app.receiver.createReplyIntent
import co.softov.morestuff.android.app.receiver.randomRequestCode
import co.softov.morestuff.android.app.receiver.setReplayIntentExtras
import co.softov.morestuff.android.data.utils.toEpochMilliseconds
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ReplyType.*
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.service.Notifier
import timber.log.Timber
import java.util.*

class NotifierImpl(
    private val context: Context
) : Notifier {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val appPerson: Person
    private val userPerson: Person

    init {
        createNotificationChannel()
        appPerson = Person.Builder().setBot(true).setImportant(true).setName("Mr.Stuff").build()
        userPerson = Person.Builder().setName("Me").build()
    }

    override fun showScheduleNotification(
        scheduleId: Long,
        message: Message
    ) {
        val builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_24dp)
                .setOnlyAlertOnce(true)

        val style = NotificationCompat.MessagingStyle(appPerson)
            .addMessage(message.content, Calendar.getInstance().timeInMillis, appPerson)

        builder.setStyle(style)

        val tomorrow = createReplyIntentWithTitle(scheduleId, TOMORROW)
        val later = createReplyIntentWithTitle(scheduleId, LATER)
        val snooze = createReplyIntentWithTitle(scheduleId, SNOOZE)
        val done = createReplyIntentWithTitle(scheduleId, DONE)

        builder
            .addAction(R.drawable.ic_send_24dp, snooze.first, snooze.second)
            .addAction(R.drawable.ic_send_24dp, tomorrow.first, tomorrow.second)
            .addAction(R.drawable.ic_send_24dp, done.first, done.second)
            .setContentIntent(createContentIntent(message.taskId))
            .setDeleteIntent(snooze.second)

        notificationManager.notify(scheduleId.toInt(), builder.build())
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

    private fun createContentIntent(
        taskId: Long
    ): PendingIntent =
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_TASK_ID, taskId)
        }.let {
            val requestCode = randomRequestCode
            Timber.d("createContentIntent, requestCode: $requestCode")
            // Use random request code for replying to reminder intent.
            PendingIntent.getActivity(context, requestCode, it, PendingIntent.FLAG_IMMUTABLE)
        }

    override fun showReminderNotificationReply(
        scheduleId: Long,
        messages: List<Message>
    ) {
        val messageStyle = getMessagingStyle(scheduleId.toInt(), messages)
        val notification: Notification =
            NotificationCompat.Builder(
                context,
                NOTIFICATION_CHANNEL_ID
            )
                .setStyle(messageStyle)
                .setSmallIcon(R.drawable.ic_chat_24dp)
                .setOnlyAlertOnce(true)
                .setTimeoutAfter(2000L)
                .build()

        notificationManager.notify(scheduleId.toInt(), notification)
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
                val time = message.createTime.toEpochMilliseconds
                addMessage(style, message, time)
            }
        } else {
            val message = messages.last()
            val time = message.createTime.toEpochMilliseconds
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
                notificationManager.activeNotifications.firstOrNull { it.id == id }?.notification
            }
            else -> null
        }

    override fun userInteractedWithNotification(scheduleId: Long) {
        notificationManager.cancel(scheduleId.toInt())
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ReminderNotifications"
    }

    private fun createNotificationChannel() {
        if (isAtLeastVersion(Build.VERSION_CODES.O)) {
            val name = "Reminder Notifications"
            val descriptionText = "Notifications for your reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}