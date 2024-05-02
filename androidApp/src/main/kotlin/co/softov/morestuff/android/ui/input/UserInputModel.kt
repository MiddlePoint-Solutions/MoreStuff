package co.softov.morestuff.android.ui.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.data.utils.toDayStartUtcTimeMillis
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.ChatContext
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.AppMessagesProvider
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetLastMessageFlowUseCase
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.input.task.TaskInputEvent
import co.softov.morestuff.android.ui.input.task.UserInputState
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.PriorityInputUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import co.softov.morestuff.android.ui.model.map.MessageUiMapper
import co.softov.morestuff.android.ui.model.mapToDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import timber.log.Timber

@Composable
fun userInputModel(
    events: Flow<TaskInputEvent>,
    store: AppStore = koinInject(),
    getLastMessageFlowUseCase: GetLastMessageFlowUseCase = koinInject(),
    getScopesUseCase: GetScopesUseCase = koinInject(),
    createTaskUseCase: CreateTaskUseCase = koinInject(),
    timeManager: TimeManager = koinInject(),
    timeFormatter: TimeFormatter = koinInject(),
    messageUiMapper: MessageUiMapper = koinInject(),
    appMessagesProvider: AppMessagesProvider = koinInject(),
    initialState: UserInputState = UserInputState.create(timeManager, timeFormatter),
): UserInputState {

    var scopes by remember { mutableStateOf(initialState.scopes) }
    var currentScope by remember { mutableStateOf(initialState.currentScope) }
    var priorityModel by remember { mutableStateOf(initialState.priorityModel) }
    var messages by remember { mutableStateOf(initialState.messages) }
    var taskId by remember { mutableStateOf(initialState.taskId) }

    LaunchedEffect(Unit) {
        getScopesUseCase().onRight { scopeList ->
            scopes = scopeList
            currentScope = scopeList.firstOrNull { it.id == currentScope.id } ?: defaultScope
        }
    }

    LaunchedEffect(Unit) {
        Timber.d("keymessage Initializing messages")
        messages = listOf(
            createAppMessage(
                appMessagesProvider.getWhatCanIDoForYouMessage(),
                timeManager,
                messageUiMapper
            )
        )

        getLastMessageFlowUseCase(ContentType.USER_NEW_TASK)
            .onEach { Timber.d("keymessage Message fetched: $it") }
            .drop(1)
            .distinctUntilChanged { old, new -> old?.id == new?.id }
            .map { message ->
                Timber.d("keymessage mapped message: $message")
                message?.let(messageUiMapper::map) }
            .collect { mappedMessage ->
                Timber.d("keymessage Preparing to handle mapped message: $mappedMessage")
                mappedMessage?.let {
                    Timber.d("keymessage Handling message: $it")
                    messages = listOf(it) + messages
                    delay(1500)
                    messages = listOf(
                        createAppMessage(
                            appMessagesProvider.getNewTaskAddedMessage(),
                            timeManager,
                            messageUiMapper
                        )
                    ) + messages
                    Timber.d("keymessage New task message added")
                } ?: Timber.d("keymessage Received a null mapped message")
            }
    }


    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is TaskInputEvent.SetCurrentScope -> {
                    currentScope = scopes.first { it.id == event.scopeId }
                }

                is TaskInputEvent.SetNowPriority -> {
                    priorityModel = PriorityInputUiModel(
                        priority = PriorityUiModel.Now,
                        planTime = createPlanTime(timeManager, timeFormatter)
                    )
                }

                is TaskInputEvent.SetLaterPriority -> {
                    priorityModel = PriorityInputUiModel(
                        priority = PriorityUiModel.Later,
                        planTime = createPlanTime(timeManager, timeFormatter)
                    )
                }

                is TaskInputEvent.SetPlanPriority -> {
                    priorityModel = PriorityInputUiModel(
                        priority = PriorityUiModel.Plan(localDateTime = timeManager.getDefaultPlanTime()),
                        planTime = createPlanTime(timeManager, timeFormatter)
                    )
                    Timber.d("keymessage Priority model updated to Plan with date: ${priorityModel.planTime.scheduleLocalDateTime}")
                }

                is TaskInputEvent.UpdatePlanDate -> {
                    val updatedTime = timeManager.utcMillisToLocalDateTime(
                        event.utcTimeMillis,
                        priorityModel.planTime.hour,
                        priorityModel.planTime.minute
                    )
                    priorityModel = priorityModel.copy(
                        planTime = createPlanTime(timeManager, timeFormatter, updatedTime)
                    )
                }

                is TaskInputEvent.UpdatePlanTime -> {
                    val updatedTime = timeManager.localDateTime(
                        priorityModel.planTime.scheduleLocalDateTime,
                        event.hour, event.minute
                    )
                    priorityModel = priorityModel.copy(
                        priority = PriorityUiModel.Plan(updatedTime),
                        planTime = createPlanTime(timeManager, timeFormatter, updatedTime)
                    )
                }
                /*is TaskInputEvent.CreateNewTask -> {
                    val priorityDomain = priorityModel.mapToDomain()
                    Timber.d("keymessage Creating task with title: ${event.title} and priority: $priorityDomain")
                    val title = event.title.trim()
                    val params = TaskParams(title, priorityDomain, TaskType.User, currentScope.id)
                    val task = createTaskUseCase(params)
                    taskId = task.id
                }*/
                is TaskInputEvent.CreateNewTask -> {
                    Timber.d("keymessage Before creating task, priorityModel is: ${priorityModel.priority} for date ${priorityModel.planTime.scheduleLocalDateTime}")
                    val priorityDomain = priorityModel.mapToDomain()
                    val title = event.title.trim()
                    val params = TaskParams(title, priorityDomain, TaskType.User, currentScope.id)
                    val task = createTaskUseCase(params)
                    Timber.d("keymessage Creating task with title: ${event.title} and priority: $params")

                    taskId = task.id
                    store.dispatch(TaskAction.TaskCreatedAction(task, priorityDomain))
                }

            }
        }
    }

    return UserInputState(
        scopes = scopes,
        currentScope = currentScope,
        priorityModel = priorityModel,
        messages = messages,
        taskId = taskId
    )
}

private fun createAppMessage(
    content: String,
    timeManager: TimeManager,
    messageUiMapper: MessageUiMapper
): MessageUiModel {
    val message = Message(
        id = timeManager.nowUtcMillis,
        contentType = ContentType.APP_TASK_MESSAGE,
        createTime = timeManager.getCreateTime(),
        content = content
    )
    return messageUiMapper.map(message)
}

fun createPlanTime(
    timeManager: TimeManager,
    timeFormatter: TimeFormatter,
    time: LocalDateTime = timeManager.getDefaultPlanTime()
): ScheduleUiModel = ScheduleUiModel(
    scheduleLocalDateTime = time,
    displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
    displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "--:--",
    scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
    dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
    currentUtcTimeMillis = timeManager.nowUtcMillis
)

