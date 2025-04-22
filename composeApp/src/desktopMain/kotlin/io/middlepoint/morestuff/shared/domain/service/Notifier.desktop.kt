package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message

class NotifierImpl : Notifier {
    override fun showReminderNotification(message: Message) {
        TODO("Not yet implemented")
    }

    override fun showReminderNotifications(messages: List<Message>) {
        TODO("Not yet implemented")
    }

    override fun showReminderNotificationReply(scheduleId: Long, messages: List<Message>) {
        TODO("Not yet implemented")
    }

    override fun clearScheduleNotification(scheduleId: Uuid) {
        TODO("Not yet implemented")
    }

    override fun showReviewNotification() {
        TODO("Not yet implemented")
    }

    override fun cancelReminderNotifications() {
        TODO("Not yet implemented")
    }

    override fun getActiveNotificationScheduleIds(): List<Long> {
        return listOf()
    }
}