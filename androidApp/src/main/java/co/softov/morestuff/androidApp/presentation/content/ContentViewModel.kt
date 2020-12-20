package co.softov.morestuff.androidApp.presentation.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.*
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.UserResponseAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.CreateTaskAction
import co.softov.morestuff.androidApp.domain.usecase.message.GetPagedMessages
import co.softov.morestuff.androidApp.presentation.content.ContentViewEvent.*
import kotlinx.coroutines.launch

class ContentViewModel(
    private val getPagedMessages: GetPagedMessages,
    private var conductor: ContentConductor?
) : BaseViewModel<ContentViewState, ContentViewEvent>(ContentViewState()) {

    override var enableDebug = false

    private lateinit var messageLiveData: LiveData<PagedList<Message>>
    private val messagePageObserver: Observer<PagedList<Message>> = Observer {
        sendEvent(ShowChatData(it))
    }

    override fun onLoadData() {
        sendEvent(Init)
        setupChatMessagesPaging()
    }

    private fun setupChatMessagesPaging() {
        viewModelScope.launch {
            getPagedMessages().map { messages ->
                messageLiveData = messages.toLiveData(
                    PagedList.Config.Builder().apply {
                        setPageSize(10)
                        setEnablePlaceholders(false)
                    }.build()
                )
                messageLiveData.observeForever(messagePageObserver)
            }
        }
    }

    override fun onAppStateChange(state: AppState) {

    }

    override fun onReduceState(event: ContentViewEvent): ContentViewState {
        return when (event) {
            is Init -> state // TODO: set user default priority?
            is ShowChatData -> state.copy(data = event.data)
            is ChangePriority -> {
                val priorityItems = when (event.priority) {
                    is Priority.Today -> Priority.Today(TodayOption.Automatic)
                    is Priority.Tomorrow -> Priority.Tomorrow(TomorrowOption.Automatic)
                    is Priority.Later -> Priority.Later(LaterOption.Automatic)
                }

                state.copy(
                    priority = event.priority,
                    currentTimeOptionId = 0
                )
            }
        }
    }

    fun addNewTask(title: String) {
        dispatchAppStoreAction(CreateTaskAction(title, state.priority))
    }

    fun selectedTodayPriority() {
        sendEvent(ChangePriority(Priority.Today()))
    }

    fun selectedTomorrowPriority() {
        sendEvent(ChangePriority(Priority.Tomorrow()))
    }

    fun selectedLaterPriority() {
        sendEvent(ChangePriority(Priority.Later()))
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        dispatchAppStoreAction(UserResponseAction(scheduleId, replyType))
    }

    fun userSelectedTimeOption(taskId: Long, option: TimeOption) {

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
        messageLiveData.removeObserver(messagePageObserver)
    }
}
