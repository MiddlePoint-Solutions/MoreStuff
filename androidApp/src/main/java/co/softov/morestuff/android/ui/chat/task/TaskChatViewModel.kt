package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.*
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskChatViewModel(
    getMessages: GetMessages,
    getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    private val getTaskFlow: GetTaskFlowUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val taskId: Long,
    private val messageRepository: MessageRepository
) : NoStateViewModel() {

    val messages: StateFlow<List<Message>> =
        getMessages(taskId = taskId)
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
    fun sendMessage(content: String) {
        viewModelScope.launch {
            val result = messageRepository.createMessage(
                taskId = taskId,
                scheduleId = 0,
                contentType = ContentType.TASK_MESSAGE.value,
                content = content,
            )
        }
    }


    fun onBackPressed() {
        router.exit()
    }
}