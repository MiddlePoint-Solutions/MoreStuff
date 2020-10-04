package co.softov.morestuff.androidApp.presentation.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.androidApp.domain.Debug
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.usecase.message.GetPagedMessages
import co.softov.morestuff.androidApp.domain.usecase.task.CreateTask
import co.softov.morestuff.androidApp.domain.usecase.task.TaskParams
import co.softov.morestuff.androidApp.presentation.content.ContentViewEvent.ChangePriority
import co.softov.morestuff.androidApp.presentation.content.ContentViewEvent.Init
import co.softov.morestuff.androidApp.presentation.content.ContentViewEvent.SetCustomTime
import co.softov.morestuff.androidApp.presentation.content.ContentViewEvent.SetTimeOption
import co.softov.morestuff.androidApp.presentation.content.ContentViewEvent.ShowChatData
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.CUSTOM
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.MORNING
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.ONE_HOUR
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption.SOME_DAY
import co.softov.morestuff.androidApp.presentation.dashboard.options.createLaterOptions
import co.softov.morestuff.androidApp.presentation.dashboard.options.createTodayOptions
import co.softov.morestuff.androidApp.presentation.dashboard.options.createTomorrowOptions

class ContentViewModel(
    private val getPagedMessages: GetPagedMessages,
    private val createNewTask: CreateTask,
    private val debug: Debug,
    private var conductor: ContentConductor?
) : BaseViewModel<ContentViewState, ContentViewEvent>(ContentViewState()) {

    override var enableDebug = false

    private val todayOptions = listOf(ONE_HOUR)
    private val tomorrowOptions = listOf(MORNING)
    private val laterOptions = listOf(SOME_DAY)

    private lateinit var messageLiveData: LiveData<PagedList<Message>>
    private val messagePageObserver: Observer<PagedList<Message>> = Observer {
        sendEvent(ShowChatData(it))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

    override fun onReduceState(event: ContentViewEvent): ContentViewState {
        return when (event) {
            is Init -> state.copy(timeOptions = todayOptions.createTodayOptions())
            is ShowChatData -> state.copy(data = event.data)
            is ChangePriority -> {
                val priorityItems = when (event.priority) {
                    is Priority.Later -> laterOptions.createLaterOptions()
                    is Priority.Today -> todayOptions.createTodayOptions()
                    is Priority.Tomorrow -> tomorrowOptions.createTomorrowOptions()
                }

                state.copy(
                    priority = event.priority,
                    timeOptions = priorityItems,
                    currentTimeOptionId = 0
                )
            }
            is SetTimeOption -> state.copy(currentTimeOptionId = event.optionId)
            is SetCustomTime -> {
                val optionId = state.timeOptions.first { it.option == CUSTOM }.id
                val optionsUpdate = state.timeOptions.map { optionItem ->
                    if (optionItem.option == CUSTOM) {
                        optionItem.copy(time = event.time)
                    } else optionItem
                }
                state.copy(
                    currentTimeOptionId = optionId,
                    timeOptions = optionsUpdate
                )
            }
        }
    }

    fun userSelectedTimeOption(optionId: Long) {
        when (state.timeOptions[optionId.toInt()].option) {
            CUSTOM -> when (state.priority) {
                is Priority.Later -> conductor?.showDateTimePicker()
                is Priority.Today -> conductor?.showTodayTimePicker()
                is Priority.Tomorrow -> conductor?.showTomorrowTimePicker()
            }
            else -> sendEvent(SetTimeOption(optionId))
        }
    }

    fun userSetCustomTime(time: Long) {
        sendEvent(SetCustomTime(time))
    }

    fun addNewTask(title: String) {
        viewModelScope.launch {
            val params = TaskParams(title, taskTime, taskPriority)
            createNewTask(params)
        }
    }

    private val taskTime: Long
        get() = when {
            debug.debugReminders -> {
                if (state.priority is Priority.Today) {
                    debug.todayDebugTime
                } else {
                    state.timeOptions.first().time
                }
            }
            else -> {
                state.timeOptions.first().time
            }
        }

    private val taskPriority: Priority
        get() = when(state.priority) {
            is Priority.Later -> Priority.Later(taskTime)
            is Priority.Today -> Priority.Today(taskTime)
            is Priority.Tomorrow -> Priority.Tomorrow(taskTime)
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

    fun showTaskList() {
        conductor?.showTaskList()
    }

    override fun onCleared() {
        super.onCleared()
        conductor = null
        messageLiveData.removeObserver(messagePageObserver)
    }
}
