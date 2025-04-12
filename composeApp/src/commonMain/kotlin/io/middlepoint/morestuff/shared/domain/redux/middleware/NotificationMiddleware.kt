package io.middlepoint.morestuff.shared.domain.redux.middleware

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.middleware.NotificationAction.RemoveScheduleNotificationAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.NotificationAction.ShowReminderNotificationAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.NotificationAction.ShowReviewNotification
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.usecase.task.ClearTaskNotificationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class NotificationAction : Action.FeatureAction() {
    internal data class ShowReminderNotificationAction(
      val message: Message,
    ) : NotificationAction()

    data object ShowReviewNotification : NotificationAction()

    internal data class RemoveScheduleNotificationAction(
        val scheduleId: Long,
    ) : NotificationAction()
}

class NotificationMiddleware(
    private val notifier: Notifier,
    private val clearTaskNotificationsUseCase: ClearTaskNotificationsUseCase,
    //private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase,
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

            is TaskAction.CompleteTasksAction -> {
                if (action.complete) scope.launch {
                    clearTaskNotificationsUseCase(action.taskIds)
                }
            }

           /* is SettingAction.SetReviewTimeAction -> scope.launch {
                updateReviewNotificationScheduleUseCase(
                    action.hour,
                    action.minute
                )
            }*/

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}