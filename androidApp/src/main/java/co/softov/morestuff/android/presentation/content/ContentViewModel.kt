package co.softov.morestuff.android.presentation.content

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction.CreateTaskAction
import co.softov.morestuff.android.domain.usecase.message.GetPagedMessages
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContentViewModel(
    private val getPagedMessages: GetPagedMessages,
    private val messageMap: MessageDbMapper,
    private var conductor: ContentConductor?
) : ViewModel(), KoinComponent {

    private val store: AppStore by inject()
    private val router: Router by inject()

    private val _priorityState = MutableStateFlow<Priority>(Priority.Today())

    val priorityState: StateFlow<Priority>
        get() = _priorityState

    val messagePager = Pager(PagingConfig(pageSize = 20)) {
        getPagedMessages()
    }.flow.map { messageData ->
        messageData.map { message -> messageMap(message) }
    }

    /*fun onReduceState(event: ContentViewEvent): ContentViewState {
        return when (event) {
            is ShowChatData -> state.copy(data = event.data)
            is ChangePriority -> {
                val priorityItems = when (event.priority) {
                    is Priority.Today -> Priority.Today()
                    is Priority.Tomorrow -> Priority.Tomorrow()
                    is Priority.Later -> Priority.Later()
                }

                state.copy(
                    priority = event.priority,
                    currentTimeOptionId = 0
                )
            }
        }
    }*/

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

    fun userSelectedPriorityOption(taskId: Long, option: PriorityOption) {

    }

    fun showTaskList() {
        conductor?.showTaskList()
    }

    fun onBackPressed() {
        router.exit()
    }

    override fun onCleared() {
        super.onCleared()
        conductor = null
    }
}
