package co.softov.morestuff.android.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.PriorityAction
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class TaskChatViewModel(
    private val store: AppStore,
    private val getMessages: GetMessages,
    private val getTaskFlowUseCase: GetTaskFlowUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val taskId: Long,
) : ViewModel(), KoinComponent {

    private val router: Router by inject()

    private val _messages = MutableStateFlow<List<Message>>(listOf())
    val messages: StateFlow<List<Message>> get() = _messages

    val task: StateFlow<Task> =
        getTaskFlowUseCase(taskId = taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = Task.empty()
            )

    var taskTitle by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            taskTitle = getTaskFlowUseCase(taskId = taskId).first().title
            getMessages(taskId = taskId)
                .onEach { _messages.value = it }
                .launchIn(this)
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

    fun onBackPressed() {
        router.exit()
    }

}
