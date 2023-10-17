package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.RemoveScheduleNotificationAction
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.ShowReminderNotificationAction
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.ShowReviewNotification
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.usecase.schedule.UpdateReviewNotificationScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.ClearTaskNotificationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class NotificationAction : Action.FeatureAction() {
    internal data class ShowReminderNotificationAction(
        val message: Message,
    ) : NotificationAction()

    internal data object ShowReviewNotification : NotificationAction()

    internal data class RemoveScheduleNotificationAction(
        val scheduleId: Long,
    ) : NotificationAction()
}

class NotificationMiddleware(
    private val notifier: Notifier,
    private val clearTaskNotificationsUseCase: ClearTaskNotificationsUseCase,
    private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
    ): Action {
        when (action) {

            is ShowReminderNotificationAction -> {
                notifier.showReminderNotification(action.message)
            }

            is ShowReviewNotification -> {
                notifier.showReviewNotification()
            }

            is RemoveScheduleNotificationAction -> {
                notifier.clearScheduleNotification(action.scheduleId)
            }

            is ReminderAction.UserResponseAction -> {
                notifier.clearScheduleNotification(action.scheduleId)
            }

            is TaskAction.CompleteTaskAction -> {
                if (action.complete) scope.launch {
                    clearTaskNotificationsUseCase(action.taskId)
                }
            }

            is SettingAction.SetReviewTimeAction -> scope.launch {
                updateReviewNotificationScheduleUseCase(
                    action.hour,
                    action.minute,
                    action.replaceExisting
                )
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}