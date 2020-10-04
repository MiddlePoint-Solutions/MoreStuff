package co.softov.morestuff.androidApp.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.isAtLeastVersion
import co.softov.morestuff.androidApp.app.receiver.NotificationReceiver
import co.softov.morestuff.androidApp.app.receiver.createReplyIntent
import co.softov.morestuff.androidApp.domain.enums.Message
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.enums.ReplyType.*
import co.softov.morestuff.androidApp.domain.service.Notifier
import java.util.Calendar

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
        message: co.softov.morestuff.androidApp.domain.model.Message
    ) {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_chat_24dp)
            .setOnlyAlertOnce(true)

        val style = NotificationCompat.MessagingStyle(appPerson)
            .addMessage(message.content, Calendar.getInstance().timeInMillis, appPerson)

        builder.setStyle(style)

        val tomorrow = createReplyIntentWithTitle(scheduleId, TOMORROW)
        val later = createReplyIntentWithTitle(scheduleId, LATER)
        val snooze = createReplyIntentWithTitle(scheduleId, SNOOZE)
        val done = createReplyIntentWithTitle(scheduleId, DONE)
        val cancel = createReplyIntentWithTitle(scheduleId, CANCEL)

        val random = listOf(tomorrow, later).shuffled().first()

        builder
            .addAction(R.drawable.ic_send_24dp, snooze.first, snooze.second)
            .addAction(R.drawable.ic_send_24dp, random.first, random.second)
            .addAction(R.drawable.ic_send_24dp, done.first, done.second)
            .setDeleteIntent(cancel.second)

        notificationManager.notify(scheduleId.toInt(), builder.build())
    }

    private fun createReplyIntentWithTitle(
        scheduleId: Long,
        type: ReplyType
    ): Pair<String, PendingIntent> {
        val actionText = when (type) {
            CANCEL -> "Cancel"
            SNOOZE -> "1 Hour"
            TOMORROW -> "Tomorrow"
            LATER -> "Later"
            DONE -> "Done"
            NONE -> ""
        }
        return actionText to NotificationReceiver.createReplyIntent(context, scheduleId, type)
    }

    override fun showReminderNotificationReply(
        scheduleId: Long,
        messages: List<co.softov.morestuff.androidApp.domain.model.Message>
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
        messages: List<co.softov.morestuff.androidApp.domain.model.Message>
    ): NotificationCompat.MessagingStyle {

        var style = getActiveNotificationById(notificationId)?.let {
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(it)
        }

        if (style == null) {
            style = NotificationCompat.MessagingStyle(appPerson)
            for (message in messages) {
                style.addMessage(
                    message.content, message.createTime, getMessagePerson(message.type)
                )
            }
        } else {
            val last = messages.last()
            style.addMessage(last.content, last.createTime, getMessagePerson(last.type))
        }

        return style
    }

    private fun getMessagePerson(messageType: Message): Person {
        return when (messageType) {
            Message.CONFIRM_NEW_TASK,
            Message.TASK_REMINDER -> appPerson
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