package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction.*
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction.RemoveScheduleNotificationAction
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction.ShowReminderNotificationAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.usecase.task.ClearTaskNotificationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotificationMiddleware(
    private val notifier: Notifier,
    private val clearTaskNotificationsUseCase: ClearTaskNotificationsUseCase,
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

            is UserResponseAction -> with(action) {
                notifier.clearScheduleNotification(scheduleId)
                dispatch(ScheduleAction.ScheduleReplyAction(scheduleId, replyType))
            }

            is TaskAction.CompleteTasksAction -> {
                if (action.complete) scope.launch {
                    clearTaskNotificationsUseCase(action.taskIds)
                }
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}