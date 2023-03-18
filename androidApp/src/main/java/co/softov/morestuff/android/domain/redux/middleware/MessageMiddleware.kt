package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.CreateScheduleNotificationAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ScheduleReplyAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.message.CreateScheduleMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateTaskMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.SetScheduleMessageResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MessageAction : Action.FeatureAction() {
    internal data class CreateScheduleMessageAction(val scheduleId: Long) : MessageAction()
}

class MessageMiddleware(
    private val createTaskMessageUseCase: CreateTaskMessageUseCase,
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase,
    private val setScheduleResponseMessage: SetScheduleMessageResponse
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {

        when (action) {
            is TaskAction.TaskCreatedAction -> scope.launch {
                createTaskMessageUseCase(action.task)
                createTaskConfirmationMessageUseCase(action.task.id, action.priority)
            }

            is ScheduleAction.RescheduleTaskAction -> scope.launch {
                val (title, replyType) = when (action.priority) {
                    is Priority.Later -> "Later" to ReplyType.LATER
                    is Priority.Today -> "Snooze" to ReplyType.SNOOZE
                    is Priority.Tomorrow -> "Tomorrow" to ReplyType.TOMORROW
                }
                setScheduleResponseMessage(action.taskId, title, replyType)
            }

            is TaskAction.SetTaskComplete -> scope.launch {
                setScheduleResponseMessage(action.taskId, "Done", ReplyType.DONE)
            }

            is CreateScheduleMessageAction -> scope.launch {
                createScheduleMessageUseCase(action.scheduleId).map { message ->
                    dispatch(CreateScheduleNotificationAction(action.scheduleId, message))
                }
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}