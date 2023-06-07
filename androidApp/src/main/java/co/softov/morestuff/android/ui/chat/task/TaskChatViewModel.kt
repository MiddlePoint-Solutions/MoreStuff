package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.usecase.message.FetchOpenGraphMetadataUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskChatViewModel(
    getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase,
    private val getTaskFlow: GetTaskFlowUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val taskId: Long,
    private val messageRepository: MessageRepository,
    private val fetchOpenGraphMetadataUseCase: FetchOpenGraphMetadataUseCase,
    devTools: DevTools,
) : NoStateViewModel() {

    val messages: StateFlow<List<Message>> = flow {
        while (true) {
            val showDebug = devTools.getDebugMessageSwitchState()
            val messages = if (showDebug) {
                getTaskMessagesFlowUseCase(taskId = taskId).first()
            } else {
                getTaskChatMessagesUseCase(taskId = taskId).first()
            }
            emit(messages)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )


    val task: StateFlow<TaskDomain> =
        getTaskFlow(taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = TaskDomain()
            )

    val schedule: StateFlow<ScheduleDomain?> =
        getActiveScheduleFlow(taskId)
            .map { it.getOrElse { null } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    var taskTitle by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            taskTitle = getTaskFlow(taskId = taskId).first().title
        }
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        store.dispatch(UserResponseAction(scheduleId, replyType))
    }

    fun updateTaskTitle(title: String) {
        taskTitle = title
        if (title.isNotEmpty()) {
            viewModelScope.launch {
                updateTaskTitleUseCase(taskId, title)
            }
        }
    }

    fun setTaskComplete(complete: Boolean) {
        store.dispatch(TaskAction.CompleteTaskAction(taskId = taskId, complete))
    }

    fun sendMessageForTask(content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }


    fun onBackPressed() {
        router.exit()
    }
}