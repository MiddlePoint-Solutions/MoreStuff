package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Defaults
import co.softov.morestuff.android.domain.model.replyWithTitle
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.ShowReminderNotificationAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MessageAction : Action.FeatureAction() {
    internal data class CreateScheduleMessageAction(val scheduleId: Long) : MessageAction()
}

class MessageMiddleware(
    private val createTaskMessageUseCase: CreateTaskMessageUseCase,
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase,
    private val setScheduleResponseMessage: SetScheduleMessageResponse,
    private val countActiveReminderMessages: CountActiveReminderMessages,
    private val getActiveScheduleMessages: GetActiveScheduleMessages,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {

        when (action) {
            is CreateScheduleMessageAction -> scope.launch {
                createScheduleMessageUseCase(action.scheduleId).map { message ->
                    dispatch(ShowReminderNotificationAction(message))
                    // TODO: show task overload notification?
//                    val activeMessages = getActiveScheduleMessages()
//                    if (activeMessages.size > Defaults.REMINDER_GROUP_LIMIT) {
//                        dispatch(NotificationAction.ShowReminderNotificationsAction(activeMessages))
//                    }
                }
            }

            is TaskAction.TaskCreatedAction -> scope.launch {
                createTaskMessageUseCase(action.task)
                createTaskConfirmationMessageUseCase(action.task.id, action.priority)
            }

            is ScheduleAction.RescheduleTaskAction -> scope.launch {
                val (title, replyType) = action.priority.replyWithTitle
                setScheduleResponseMessage(action.taskId, title, replyType)
            }

            is ScheduleAction.RescheduleTasksAction -> scope.launch {
                val (title, replyType) = action.priority.replyWithTitle
                action.taskIds.forEach {
                    setScheduleResponseMessage(it, title, replyType)
                }
            }

            is TaskAction.CompleteTaskAction -> scope.launch {
                setScheduleResponseMessage(action.taskId, "Done", ReplyType.DONE)
            }

            is TaskAction.CompleteTasksAction -> scope.launch {
                action.taskIds.forEach {
                    setScheduleResponseMessage(it, "Done", ReplyType.DONE)
                }
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}