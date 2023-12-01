package co.softov.morestuff.android.domain.redux.middleware

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.displayTitle
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.Dispatch
import co.softov.morestuff.android.domain.redux.store.Next
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.ShowReminderNotificationAction
import co.softov.morestuff.android.domain.redux.state.TaskAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.message.CreateImageMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateScheduleMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.DeleteMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.SetScheduleMessageResponseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MessageAction : Action.FeatureAction() {
    internal data class CreateScheduleMessageAction(val scheduleId: Long) : MessageAction()
    internal data class CreateUserTaskMessageAction(val taskId: Long, val content: String) :
        MessageAction()

    internal data class CreateImageMessageAction(
        val taskId: Long,
        val filePath: String,
        val message: String
    ) : MessageAction()

    data class DeleteMessageAction(val messageId: Long) : MessageAction()
}

class MessageMiddleware(
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val setScheduleResponseMessage: SetScheduleMessageResponseUseCase,
    private val createImageMessageUseCase: CreateImageMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope,
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

            is MessageAction.CreateUserTaskMessageAction -> scope.launch {
                createMessageUseCase(
                    action.taskId,
                    action.content,
                    ContentType.TASK_MESSAGE,
                    messageData = null
                )
            }

            is MessageAction.CreateImageMessageAction -> scope.launch {
                createImageMessageUseCase(
                    action.taskId,
                    scheduleId = 0,
                    contentType = ContentType.TASK_MESSAGE,
                    filePath = action.filePath,
                    action.message
                )
            }

            is MessageAction.DeleteMessageAction -> scope.launch {
                deleteMessageUseCase(action.messageId)
            }

            is TaskAction.TaskCreatedAction -> scope.launch {
                createMessageUseCase(
                    action.task.id,
                    title = action.task.title,
                    contentType = ContentType.USER_NEW_TASK,
                    messageData = null
                )
                createTaskConfirmationMessageUseCase(action.task.id, action.priority)
            }

            is ScheduleAction.ScheduleReplyAction -> scope.launch {
                with(action) {
                    val title = replyType.displayTitle
                    setScheduleResponseMessage(listOf(schedule.taskId), title, replyType)
                }
            }

            is TaskAction.CompleteTasksAction -> scope.launch {
                setScheduleResponseMessage(action.taskIds, "Done", ReplyType.DONE)
            }


            else -> NoOp
        }

        return next(state, action, dispatch)
    }
}