package io.middlepoint.morestuff.shared.ui.components.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.ChatContext
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.defaultScope
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.service.AppMessageProvider
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.TaskParams
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.PriorityUiModel
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.shared.ui.model.mapToDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject

@Composable
fun userInputModel(
  initialState: UserInputState,
  context: ChatContext,
  events: Flow<UserInputEvent>,
  store: AppStore = koinInject(),
  getScopesUseCase: GetScopesUseCase = koinInject(),
  createTaskUseCase: CreateTaskUseCase = koinInject(),
  timeManager: TimeManager = koinInject(),
  timeFormatter: TimeFormatter = koinInject(),
  messageUiMapper: MessageUiMapper = koinInject(),
  appMessageProvider: AppMessageProvider = koinInject()
): UserInputState {

  // TODO: use UserInputState as the current state and use .copy to update state instead of updating these individually.
  var state by remember(initialState) { mutableStateOf(initialState) }
  var currentScope by remember { mutableStateOf(defaultScope) }

  LaunchedEffect(context) {
    Logger.d { "context: $context" }
    getScopesUseCase()
      .onRight { scopesList ->
        state = state.copy(
          scopes = scopesList
        )
        currentScope = scopesList.firstOrNull { it.id == context.scopeId } ?: defaultScope
      }

    val intro = createAppMessage(
      appMessageProvider.getWhatCanIDoForYouMessage(),
      timeManager,
      messageUiMapper
    )

    state = state.copy(
      messages = mutableListOf<MessageUiModel>().apply { add(0, intro) },
      priority = PriorityUiModel.Now
    )
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        UserInputEvent.SetLaterPriority -> {
          state = state.copy(
            priority = PriorityUiModel.Later
          )
        }

        UserInputEvent.SetNowPriority -> {
          state = state.copy(
            priority = PriorityUiModel.Now
          )
        }

        UserInputEvent.SetPlanPriority -> {
          val time = createPlanTime(timeManager, timeFormatter)
          state = state.copy(
            priority = PriorityUiModel.Plan(time.scheduleLocalDateTime),
            planTime = time
          )
        }

        is UserInputEvent.UpdatePlanDate -> {
          state = state.copy(
            planTime = state.planTime?.let {
              val updatedTime = timeManager.utcMillisToLocalDateTime(
                event.utcTimeMillis,
                it.hour,
                it.minute
              )
              createPlanTime(timeManager, timeFormatter, updatedTime)
            }
          )
        }

        is UserInputEvent.UpdatePlanTime -> {
          state = state.copy(
            planTime = state.planTime?.let {
              val updatedTime = timeManager.localDateTime(
                it.scheduleLocalDateTime,
                event.hour,
                event.minute
              )
              createPlanTime(timeManager, timeFormatter, updatedTime)
            }
          )
        }

        is UserInputEvent.SetCurrentScope -> {
          currentScope = state.scopes.first { it.id == event.scopeId }
        }

        is UserInputEvent.CreateNewTask -> {
          val domainPriority = state.priority.mapToDomain()
          val trimmedTitle = event.title.trim()
          val params = TaskParams(trimmedTitle, domainPriority, currentScope.id)
          val task = createTaskUseCase(params)

          store.dispatch(TaskAction.TaskCreatedAction(task, domainPriority))
          state = state.copy(lastCreatedTaskId = task.id)
          launch {
            // Hack for not using the same taskId in share screen.
            // This will be solved when adding new navigation with decompose router
            delay(300)
            state = state.copy(
              lastCreatedTaskId = null
            )
          }
        }
      }
    }
  }

  return state
}

private fun createAppMessage(
  content: String,
  timeManager: TimeManager,
  messageUiMapper: MessageUiMapper
): MessageUiModel {
  val message = Message(
    id = timeManager.nowUtcMillis,
    contentType = ContentType.APP_TASK_MESSAGE,
    createTime = timeManager.getCreatedTime(),
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
  displayDate = timeFormatter.formatDisplayDate(time.date),
  displayTime = timeFormatter.formatDisplayTime(time.time) ?: "--:--",
  scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
  dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
  currentUtcTimeMillis = timeManager.nowUtcMillis
)