package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.Message

interface Notifier {

    fun showScheduleNotification(scheduleId: Long, message: Message)
    fun showReminderNotificationReply(scheduleId: Long, messages: List<Message>)
    fun userInteractedWithNotification(scheduleId: Long)
    fun showReviewNotification()
    fun cancelReminderNotifications()

}