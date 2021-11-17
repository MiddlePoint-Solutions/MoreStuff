package co.softov.morestuff.android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.CreateTaskAction
import co.softov.morestuff.android.domain.usecase.message.GetActiveScheduleMessages
import co.softov.morestuff.android.domain.usecase.message.GetMessages
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessages
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContentViewModel(
    private val getActiveScheduleMessages: GetActiveScheduleMessages,
    private val getMessages: GetMessages,
    private var conductor: ContentConductor?
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
            getActiveScheduleMessages(TimeUtils.localDateTime1HourBack)
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
