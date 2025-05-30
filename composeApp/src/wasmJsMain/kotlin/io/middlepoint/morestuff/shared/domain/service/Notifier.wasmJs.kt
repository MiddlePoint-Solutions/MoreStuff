package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message


class NotifierImpl : Notifier {
    override fun showReminderNotification(message: Message) {

    }

    override fun clearScheduleNotification(scheduleId: Uuid) {

    }

    override fun showReviewNotification() {

    }

    override fun cancelReminderNotifications() {

    }

    override fun getActiveNotificationScheduleIds(): List<Long> {
        return listOf()
    }
}