package co.softov.morestuff.android.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.usecase.message.GetMessagesUseCase
import co.softov.morestuff.android.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
) : NoStateViewModel() {

    val messages: StateFlow<List<Message>> =
        getMessagesUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    var priorityModel by mutableStateOf<Priority>(Priority.Now())
        private set

    fun addNewTask(title: String) {
        store.dispatch(TaskAction.CreateUserTaskAction(title, priorityModel))
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        store.dispatch(UserResponseAction(scheduleId, replyType))
    }

    fun priorityChanged(priority: Priority) {
        priorityModel = priority
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
