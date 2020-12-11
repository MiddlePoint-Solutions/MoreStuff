package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.androidApp.domain.redux.middleware.NotificationAction.CreateScheduleNotificationAction
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.ScheduleReplyAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.usecase.message.CreateScheduleMessageUseCase
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.androidApp.domain.usecase.message.CreateTaskMessageUseCase
import co.softov.morestuff.androidApp.domain.usecase.message.SetScheduleResponseMessage
import co.softov.morestuff.androidApp.domain.redux.Dispatch
import co.softov.morestuff.androidApp.domain.redux.Middleware
import co.softov.morestuff.androidApp.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MessageAction: Action.FeatureAction() {
    internal data class CreateScheduleMessageAction(val scheduleId: Long): MessageAction()
}

class MessageMiddleware(
    private val createTaskMessageUseCase: CreateTaskMessageUseCase,
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase,
    private val setScheduleResponseMessage: SetScheduleResponseMessage
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {

        when (action) {
            is TaskCreatedAction -> scope.launch {
                createTaskMessageUseCase(action.task)
                createTaskConfirmationMessageUseCase(action.task.id, action.priority)
            }

            is ScheduleReplyAction -> scope.launch {
                val title = when (action.replyType) {
                    ReplyType.LATER -> "Later"
                    ReplyType.SNOOZE -> "Snooze"
                    ReplyType.TOMORROW -> "Tomorrow"
                    ReplyType.DONE -> "Done"
                }
                setScheduleResponseMessage(action.schedule.taskId, title, action.replyType)
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