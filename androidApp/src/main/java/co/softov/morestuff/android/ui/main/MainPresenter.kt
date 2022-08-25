package co.softov.morestuff.android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.CreateTaskAction
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainPresenter(
    private val getMessages: GetMessages,
    private var conductor: MainConductor?
) : ViewModel(), KoinComponent {

    private val store: AppStore by inject()

    private val _messages = MutableStateFlow<List<Message>>(listOf())
    val messages: StateFlow<List<Message>>
        get() = _messages

    private val _priorityState = MutableStateFlow<Priority>(Priority.Today())
    val priorityState: StateFlow<Priority>
        get() = _priorityState

    init {
        viewModelScope.launch {
            getMessages()
                .onEach { _messages.value = it }
                .launchIn(this)
        }
    }

    fun addNewTask(title: String) {
        store.dispatch(CreateTaskAction(title, priorityState.value))
    }

    fun selectedTodayPriority() {
        _priorityState.value = Priority.Today()
    }

    fun selectedTomorrowPriority() {
        _priorityState.value = Priority.Tomorrow()
    }

    fun selectedLaterPriority() {
        _priorityState.value = Priority.Later()
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        store.dispatch(UserResponseAction(scheduleId, replyType))
    }

    fun showTaskList() {
        conductor?.showTaskList()
    }

    override fun onCleared() {
        super.onCleared()
        conductor = null
    }
}
