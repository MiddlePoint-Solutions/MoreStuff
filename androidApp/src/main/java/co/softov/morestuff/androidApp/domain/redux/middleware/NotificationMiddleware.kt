package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.NotificationAction.CreateScheduleNotificationAction
import co.softov.morestuff.androidApp.domain.redux.middleware.NotificationAction.RemoveScheduleNotificationAction
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.androidApp.domain.service.Notifier
import co.softov.morestuff.androidApp.domain.redux.Dispatch
import co.softov.morestuff.androidApp.domain.redux.Middleware
import co.softov.morestuff.androidApp.domain.redux.Next
import kotlinx.coroutines.CoroutineScope

sealed class NotificationAction : Action.FeatureAction() {
    internal data class CreateScheduleNotificationAction(
        val scheduleId: Long,
        val message: Message
    ) : NotificationAction()

    internal data class RemoveScheduleNotificationAction(val scheduleId: Long) : NotificationAction()
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

            is CreateScheduleNotificationAction -> {
                notifier.showScheduleNotification(action.scheduleId, action.message)
            }

            is UserResponseAction -> {
                notifier.userInteractedWithNotification(action.scheduleId)
            }

            is RemoveScheduleNotificationAction -> {
                notifier.userInteractedWithNotification(action.scheduleId)
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}