package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.toDayStartUtcTimeMillis
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.AppMessagesProvider
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetLastMessageFlowUseCase
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.home.HomeEvent
import co.softov.morestuff.android.ui.home.HomeState
import co.softov.morestuff.android.ui.input.task.TaskInputEvent
import co.softov.morestuff.android.ui.input.task.TaskInputEvent.SetLaterPriority
import co.softov.morestuff.android.ui.input.task.TaskInputEvent.SetNowPriority
import co.softov.morestuff.android.ui.input.task.TaskInputEvent.SetPlanPriority
import co.softov.morestuff.android.ui.input.task.TaskInputEvent.UpdatePlanDate
import co.softov.morestuff.android.ui.input.task.TaskInputEvent.UpdatePlanTime
import co.softov.morestuff.android.ui.input.task.UserInputState
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.PriorityInputUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel.Later
import co.softov.morestuff.android.ui.model.PriorityUiModel.Now
import co.softov.morestuff.android.ui.model.PriorityUiModel.Plan
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import co.softov.morestuff.android.ui.model.map.MessageUiMapper
import co.softov.morestuff.android.ui.model.mapToDomain
import co.softov.morestuff.android.ui.review.ReviewState
import co.softov.morestuff.android.ui.review.ReviewViewEvent
import co.softov.morestuff.android.ui.review.reviewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import timber.log.Timber


class UserInputViewModel(
    timeManager: TimeManager,
    timeFormatter: TimeFormatter,
) : MoleculeViewModel<TaskInputEvent, UserInputState>() {
    override val initialState: UserInputState = UserInputState.create(timeManager, timeFormatter)

    @Composable
    override fun models(events: Flow<TaskInputEvent>): UserInputState {
        return userInputModel(
            initialState = initialState,
            events = events,
        )
    }
}


/*
class UserInputViewModel(
    getLastMessageFlowUseCase: GetLastMessageFlowUseCase,
    private val getScopesUseCase: GetScopesUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val timeManager: TimeManager,
    private val timeFormatter: TimeFormatter,
    private val messageUiMapper: MessageUiMapper,
    private val appMessagesProvider: AppMessagesProvider
) : NoStateViewModel() {

    val messages = MutableStateFlow<List<MessageUiModel>>(listOf())
    val scopes = MutableStateFlow<List<ScopeDomain>>(listOf())

    private val _lastTaskMessage = getLastMessageFlowUseCase(ContentType.USER_NEW_TASK)
        .drop(1)
        .distinctUntilChanged { old, new -> old?.id == new?.id }
        .map { message -> message?.let(messageUiMapper::map) }
        .onEach { message ->
            message?.let {
                messages.update { messages ->
                    messages.toMutableList().apply { add(0, it) }
                }
                delay(1500)
                messages.update { messages ->
                    messages.toMutableList().apply {
                        add(
                            0,
                            createAppMessage(appMessagesProvider.getNewTaskAddedMessage())
                        )
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val priorityModel = MutableStateFlow(
        PriorityInputUiModel(
            priority = Now,
            planTime = createPlanTime()
        )
    )

    var currentScope by mutableStateOf(defaultScope)
        private set

    fun load(context: ChatContext) {
        viewModelScope.launch {
            getScopesUseCase().onRight { scopeList ->
                scopes.value = scopeList
                currentScope = scopeList.firstOrNull { it.id == context.scopeId } ?: defaultScope
            }
        }

        // TODO: add messages according to chat context
        messages.update {
            listOf(createAppMessage(appMessagesProvider.getWhatCanIDoForYouMessage()))
        }
    }

    private fun createAppMessage(content: String): MessageUiModel {
        val message = Message(
            id = timeManager.nowUtcMillis,
            contentType = ContentType.APP_TASK_MESSAGE,
            createTime = timeManager.getCreateTime(),
            content = content,
        )
        return messageUiMapper.map(message)
    }

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        Plan(localDateTime = timeManager.getDefaultPlanTime())
    }

    private fun createPlanTime(
        time: LocalDateTime = timeManager.getDefaultPlanTime(),
    ) = ScheduleUiModel(
        scheduleLocalDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "--:--",
        scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
        dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
        currentUtcTimeMillis = timeManager.nowUtcMillis
    )

    private fun updatePlanDate(dateMillis: Long) {
        priorityModel.update { model ->
            val updatedTime = timeManager.utcMillisToLocalDateTime(
                dateMillis,
                model.planTime.hour,
                model.planTime.minute
            )
            val planTime = createPlanTime(updatedTime)
            PriorityInputUiModel(Plan(updatedTime), planTime)
        }
    }

    private fun updatePlanTime(hour: Int, minute: Int) {
        priorityModel.update { model ->
            val updatedTime = timeManager.localDateTime(
                model.planTime.scheduleLocalDateTime,
                hour,
                minute
            )
            val planTime = createPlanTime(updatedTime)
            Timber.d("updatedTime: $updatedTime \n planTime: $planTime")
            PriorityInputUiModel(Plan(updatedTime), planTime)
        }
    }


*/
/**
 * Creates a new task and returns its id.
 * The taskId can then be used for navigation.
 *
 * @return TaskId of the newly created task
 *//*

    suspend fun createNewTask(title: String): Long {
        val priority = priorityModel.value.mapToDomain()
        val trimmedTitle = title.trim()
        val params = TaskParams(trimmedTitle, priority, TaskType.User, currentScope.id)
        val task = createTaskUseCase(params)
        dispatchAppStoreAction(TaskAction.TaskCreatedAction(task, priority))
        return task.id
    }

    fun setCurrentScope(scopeId: Long) {
        currentScope = scopes.value.first { it.id == scopeId }
    }

    private fun priorityChanged(priority: PriorityUiModel) {
        priorityModel.update {
            PriorityInputUiModel(
                priority = priority,
                planTime = createPlanTime(),
            )
        }
    }

    fun onEvent(event: TaskInputEvent) {
        when (event) {
            SetLaterPriority -> priorityChanged(Later)
            SetNowPriority -> priorityChanged(Now)
            SetPlanPriority -> priorityChanged(createPlanModel())
            is UpdatePlanDate -> updatePlanDate(event.utcTimeMillis)
            is UpdatePlanTime -> updatePlanTime(event.hour, event.minute)
        }
    }

}
*/
