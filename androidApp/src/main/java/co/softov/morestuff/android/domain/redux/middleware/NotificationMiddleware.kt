package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.*
import kotlinx.coroutines.CoroutineScope

sealed class NotificationAction : Action.FeatureAction() {
    internal data class ShowReminderNotificationAction(
        val message: Message
    ) : NotificationAction()

    internal data class ShowReminderNotificationsAction(
        val messages: List<Message>
    ) : NotificationAction()

    internal data class ShowReviewNotification(
        val reviewType: ReviewNotification
    ) : NotificationAction()

    internal data class RemoveScheduleNotificationAction(
        val scheduleId: Long
    ) : NotificationAction()
}

class NotificationMiddleware(
    private val notifier: Notifier
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is ShowReminderNotificationAction -> {
                notifier.showReminderNotification(action.message)
            }

            is ShowReminderNotificationsAction -> {
                notifier.showReminderNotifications(action.messages)
            }

            is ShowReviewNotification -> {
                notifier.showReviewNotification(action.reviewType)
            }

            is RemoveScheduleNotificationAction -> {
                notifier.userInteractedWithNotification(action.scheduleId)
            }

            is ReminderAction.UserResponseAction -> {
                notifier.userInteractedWithNotification(action.scheduleId)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}