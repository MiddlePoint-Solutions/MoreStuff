package co.softov.morestuff.android.domain.redux.middleware

import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.displayTitle
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.Dispatch
import co.softov.morestuff.android.domain.redux.Next
import co.softov.morestuff.android.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction.ShowReminderNotificationAction
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.NoOp
import co.softov.morestuff.android.domain.usecase.message.CountActiveReminderMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateScheduleMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.GetActiveScheduleMessages
import co.softov.morestuff.android.domain.usecase.message.HandleImagesUseCase
import co.softov.morestuff.android.domain.usecase.message.HandleImagesUseCaseImpl
import co.softov.morestuff.android.domain.usecase.message.SetScheduleMessageResponseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MessageAction : Action.FeatureAction() {
    internal data class CreateScheduleMessageAction(val scheduleId: Long) : MessageAction()
    internal data class CreateUserTaskMessageAction(val taskId: Long, val content: String) :
        MessageAction()
    data class CreateUserTaskImageMessageAction(val taskId: Long, val images: List<Uri>, val context: Context) : MessageAction()
}

class MessageMiddleware(
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val setScheduleResponseMessage: SetScheduleMessageResponseUseCase,
    private val handleImagesUseCase: HandleImagesUseCase

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
                createMessageUseCase(action.taskId, action.content, ContentType.TASK_MESSAGE)
            }
            is MessageAction.CreateUserTaskImageMessageAction -> scope.launch {
                handleImagesUseCase(action.images, action.context, action.taskId)
            }

            is TaskAction.TaskCreatedAction -> scope.launch {
                createMessageUseCase(
                    action.task.id,
                    title = action.task.title,
                    contentType = ContentType.USER_NEW_TASK
                )
                createTaskConfirmationMessageUseCase(action.task.id, action.priority)
            }

            is ScheduleAction.ScheduleReplyAction -> scope.launch {
                with(action) {
                    val title = replyType.displayTitle
                    setScheduleResponseMessage(schedule.taskId, title, replyType)
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