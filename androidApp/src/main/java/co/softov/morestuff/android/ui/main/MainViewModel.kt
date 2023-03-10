package co.softov.morestuff.android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.store.OnResumeAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.PriorityAction
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import co.softov.morestuff.android.ui.Screens
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel(
    private val store: AppStore,
    private val getMessages: GetMessages,
    private var conductor: MainConductor?
) : ViewModel(), KoinComponent {

    private val router: Router by inject()

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
        conductor?.showTaskList()
    }

    fun showTaskChat(taskId: Long) {
        router.navigateTo(Screens.taskChat(taskId))
    }

    override fun onCleared() {
        super.onCleared()
        conductor = null
    }
}
