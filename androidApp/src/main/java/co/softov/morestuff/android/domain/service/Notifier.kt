package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.Message

interface Notifier {

    fun showReminderNotification(message: Message)
    fun showReminderNotifications(messages: List<Message>)
    fun showReminderNotificationReply(scheduleId: Long, messages: List<Message>)
    fun userInteractedWithNotification(scheduleId: Long)
    fun showReviewNotification()
    fun cancelReminderNotifications()

    companion object {
        const val REMINDERS_CHANNEL_ID = "ReminderNotifications"

        const val REVIEW_CHANNEL_ID = "ReviewNotifications"
        const val REVIEW_NOTIFICATION_ID = 424242

        const val GROUP_KEY_REMINDERS = "io.middlepoint.morestuff.REMINDERS"
    }

}