package io.middlepoint.morestuff.android.ui.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.android.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.enums.TaskType
import io.middlepoint.morestuff.android.domain.model.ChatContext
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.model.ScopeDomain
import io.middlepoint.morestuff.android.domain.model.defaultScope
import io.middlepoint.morestuff.android.domain.redux.AppStore
import io.middlepoint.morestuff.android.domain.redux.middleware.TaskAction
import io.middlepoint.morestuff.android.domain.service.AppMessageProvider
import io.middlepoint.morestuff.android.domain.service.TimeManager
import io.middlepoint.morestuff.android.domain.usecase.message.GetLastMessageFlowUseCase
import io.middlepoint.morestuff.android.domain.usecase.scope.GetScopesUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.android.domain.usecase.task.TaskParams
import io.middlepoint.morestuff.android.domain.util.TimeFormatter
import io.middlepoint.morestuff.android.ui.model.MessageUiModel
import io.middlepoint.morestuff.android.ui.model.PriorityUiModel
import io.middlepoint.morestuff.android.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.android.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.android.ui.model.mapToDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject

@Composable
fun userInputModel(
  initialState: UserInputState,
  events: Flow<UserInputEvent>,
  store: AppStore = koinInject(),
  getLastMessageFlowUseCase: GetLastMessageFlowUseCase = koinInject(),
  getScopesUseCase: GetScopesUseCase = koinInject(),
  createTaskUseCase: CreateTaskUseCase = koinInject(),
  timeManager: TimeManager = koinInject(),
  timeFormatter: TimeFormatter = koinInject(),
  messageUiMapper: MessageUiMapper = koinInject(),
  appMessageProvider: AppMessageProvider = koinInject()
): UserInputState {

  var context by remember { mutableStateOf<ChatContext?>(null) }
  var messages by remember { mutableStateOf(listOf<MessageUiModel>()) }
  var scopes by remember { mutableStateOf((listOf<ScopeDomain>())) }
  var currentScope by remember { mutableStateOf(defaultScope) }
  var priority by remember { mutableStateOf<PriorityUiModel>(PriorityUiModel.Now) }
  var planTime by remember(priority) { mutableStateOf<ScheduleUiModel?>(null) }
  var lastCreatedTaskId by remember { mutableStateOf<Long?>(null) }

  LaunchedEffect(Unit) {
    getScopesUseCase().onRight { scopesList ->
      scopes = scopesList
      currentScope = scopesList.firstOrNull { it.id == context?.scopeId } ?: defaultScope
    }
  }

  LaunchedEffect(context) {
    val intro = createAppMessage(
      appMessageProvider.getWhatCanIDoForYouMessage(),
      timeManager,
      messageUiMapper
    )
    messages = mutableListOf<MessageUiModel>().apply { add(0, intro) }

    getLastMessageFlowUseCase(ContentType.USER_NEW_TASK)
      .drop(1)
      .distinctUntilChanged { old, new -> old?.id == new?.id }
      .map { message -> message?.let(messageUiMapper::map) }
      .collect { message ->
        message?.let {
          messages = messages.toMutableList().apply { add(0, it) }
          delay(1500)

          val appMessage =
            createAppMessage( // TODO: this might be better moved into AppMessageProvider
              appMessageProvider.getNewTaskAddedMessage(),
              timeManager,
              messageUiMapper
            )
          messages = messages.toMutableList().apply { add(0, appMessage) }
        }
      }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        UserInputEvent.SetLaterPriority -> priority = PriorityUiModel.Later
        UserInputEvent.SetNowPriority -> priority = PriorityUiModel.Now
        UserInputEvent.SetPlanPriority -> {
          val time = createPlanTime(timeManager, timeFormatter)
          planTime = time
          priority = PriorityUiModel.Plan(time.scheduleLocalDateTime)
        }

        is UserInputEvent.UpdatePlanDate -> {
          planTime = planTime?.let {
            val updatedTime = timeManager.utcMillisToLocalDateTime(
              event.utcTimeMillis,
              it.hour,
              it.minute
            )
            createPlanTime(timeManager, timeFormatter, updatedTime)
          }
        }

        is UserInputEvent.UpdatePlanTime -> {
          planTime = planTime?.let {
            val updatedTime = timeManager.localDateTime(
              it.scheduleLocalDateTime,
              event.hour,
              event.minute
            )
            createPlanTime(timeManager, timeFormatter, updatedTime)
          }
        }

        is UserInputEvent.SetCurrentScope -> {
          currentScope = scopes.first { it.id == event.scopeId }
        }

        is UserInputEvent.CreateNewTask -> {
          val domainPriority = priority.mapToDomain()
          val trimmedTitle = event.title.trim()
          val params = TaskParams(trimmedTitle, domainPriority, TaskType.User, currentScope.id)
          val task = createTaskUseCase(params)
          store.dispatch(TaskAction.TaskCreatedAction(task, domainPriority))
          lastCreatedTaskId = task.id
          launch {
            // Hack for not using the same taskId in share screen.
            // This will be solved when adding new navigation with decompose router
            delay(300)
            lastCreatedTaskId = null
          }
        }

        is UserInputEvent.LoadContext -> {
          context = event.context
        }
      }
    }
  }

  return UserInputState(
    messages = messages,
    scopes = scopes,
    priority = priority,
    planTime = planTime,
    lastCreatedTaskId = lastCreatedTaskId
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
    content = content,
  )
  return messageUiMapper.map(message)
}

private fun createPlanTime(
  timeManager: TimeManager,
  timeFormatter: TimeFormatter,
  time: LocalDateTime = timeManager.getDefaultPlanTime(),
) = ScheduleUiModel(
  scheduleLocalDateTime = time,
  displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
  displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "--:--",
  scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
  dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
  currentUtcTimeMillis = timeManager.nowUtcMillis
)