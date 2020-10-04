package co.softov.morestuff.androidApp.domain.service

import co.softov.morestuff.androidApp.domain.model.Message

interface Notifier {

    fun showScheduleNotification(scheduleId: Long, message: Message)
    fun showReminderNotificationReply(scheduleId: Long, messages: List<Message>)
    fun userInteractedWithNotification(scheduleId: Long)

}