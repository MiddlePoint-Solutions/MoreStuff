package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.core.Message

interface Notifier {

    fun showReminderNotification(message: Message)
    fun showReminderNotifications(messages: List<Message>)
    fun showReminderNotificationReply(scheduleId: Long, messages: List<Message>)
    fun clearScheduleNotification(scheduleId: Long)
    fun showReviewNotification()
    fun cancelReminderNotifications()
    fun getActiveNotificationScheduleIds() : List<Long>

    companion object {
        const val REMINDERS_CHANNEL_ID = "ReminderNotifications"

        const val REVIEW_CHANNEL_ID = "ReviewNotifications"
        const val REVIEW_NOTIFICATION_ID = 424242

        const val GROUP_KEY_REMINDERS = "io.middlepoint.morestuff.REMINDERS"
    }

}