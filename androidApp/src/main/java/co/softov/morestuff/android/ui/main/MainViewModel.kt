package co.softov.morestuff.android.ui.main

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.PriorityAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import co.softov.morestuff.android.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val getMessages: GetMessages,
) : NoStateViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(listOf())
    val messages: StateFlow<List<Message>> get() = _messages

    init {
        viewModelScope.launch {
            getMessages()
                .onEach { _messages.value = it }
                .launchIn(this)
        }
    }

    fun addNewTask(title: String) {
        store.dispatch(TaskAction.CreateTask(title))
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        store.dispatch(UserResponseAction(scheduleId, replyType))
    }

    fun priorityChanged(priority: Priority) {
        store.dispatch(PriorityAction.SetPriority(priority))
    }

    fun onPriorityOptionChanged(option: PriorityOption) {
        store.dispatch(PriorityAction.SetCurrentPriorityOption(option))
    }

    fun onResume() {
        store.dispatch(OnResumeAction)
    }

    fun showTaskList() {
        router.showBottomSheet(Screens.taskLists)
    }

    fun showTaskChat(taskId: Long) {
        router.navigateTo(Screens.taskChat(taskId))
    }
}
