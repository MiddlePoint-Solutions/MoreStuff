package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.message.CreateMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskChatViewModel(
    getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    private val getTaskFlow: GetTaskFlowUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val taskId: Long,
    private val createMessageUseCase: CreateMessageUseCase,
) : NoStateViewModel() {

    val messages: StateFlow<List<Message>> =
        getTaskChatMessagesUseCase(taskId = taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    val task: StateFlow<Task> =
        getTaskFlow(taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = Task.empty()
            )

    val schedule: StateFlow<Schedule?> =
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
        viewModelScope.launch {
            createMessageUseCase(
                taskId = taskId,
                scheduleId = 0,
                title = content,
                contentType = ContentType.TASK_MESSAGE,
            )
        }
    }


    fun onBackPressed() {
        router.exit()
    }
}