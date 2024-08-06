package io.middlepoint.morestuff.shared.domain.redux.middleware

import com.mohamedrejeb.calf.io.KmpFile
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.enums.displayTitle
import io.middlepoint.morestuff.shared.domain.redux.AppState
import io.middlepoint.morestuff.shared.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.NotificationAction.ShowReminderNotificationAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMediaMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreatePDFMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateScheduleMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateTaskConfirmationMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.DeleteMessageUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.SetScheduleMessageResponseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class MessageAction : Action.FeatureAction() {
    data class CreateScheduleMessageAction(val scheduleId: Long) : MessageAction()
    data class CreateUserTaskMessageAction(val taskId: Long, val content: String) :
        MessageAction()

    data class CreateFileMessageAction(
        val taskId: Long,
        val file: KmpFile,
        val message: String
    ) : MessageAction()

    data class CreatePDFMessageAction(
        val taskId: Long,
        val file: KmpFile,
        val message: String
    ) : MessageAction()

    data class DeleteMessageAction(val messageId: Long) : MessageAction()
}

class MessageMiddleware(
    private val createTaskConfirmationMessageUseCase: CreateTaskConfirmationMessageUseCase,
    private val createScheduleMessageUseCase: CreateScheduleMessageUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val setScheduleResponseMessage: SetScheduleMessageResponseUseCase,
    private val createMediaMessageUseCase: CreateMediaMessageUseCase,
    private val createPDFMessageUseCase: CreatePDFMessageUseCase,
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

            is MessageAction.CreateFileMessageAction -> scope.launch {
                createMediaMessageUseCase(
                    action.taskId,
                    scheduleId = 0,
                    contentType = ContentType.TASK_MESSAGE,
                    mediaFile = action.file,
                    action.message
                )
            }

            is MessageAction.CreatePDFMessageAction -> scope.launch {
                createPDFMessageUseCase(
                    taskId = action.taskId,
                    scheduleId = 0,
                    contentType = ContentType.TASK_MESSAGE,
                    pdfFile = action.file,
                    message = action.message
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