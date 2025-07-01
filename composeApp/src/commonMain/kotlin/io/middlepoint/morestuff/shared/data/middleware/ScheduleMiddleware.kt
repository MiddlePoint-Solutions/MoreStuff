package io.middlepoint.morestuff.shared.data.middleware

import io.middlepoint.morestuff.shared.data.service.TimeManagerImpl
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.Middleware
import io.middlepoint.morestuff.shared.domain.redux.action.MessageAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.CancelActiveScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.CancelScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.CreateReminderScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.ExecuteScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.RescheduleTaskAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.ScheduleCreatedAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction.ToggleReminderScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.action.UserAction
import io.middlepoint.morestuff.shared.domain.redux.state.AppState
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.redux.store.Dispatch
import io.middlepoint.morestuff.shared.domain.redux.store.Next
import io.middlepoint.morestuff.shared.domain.redux.store.NoOp
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CancelActiveScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateOneTimeScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.CreateScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleAtTimeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleUserReplyUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.ScheduleWorkUseCase
import io.middlepoint.morestuff.shared.domain.usecase.schedule.SetScheduleFulfilledUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ScheduleMiddleware(
  private val scheduleAtTimeUseCase: ScheduleAtTimeUseCase,
  private val scheduleWorkUseCase: ScheduleWorkUseCase,
  private val getScheduleUseCase: GetScheduleUseCase,
  private val createScheduleUseCase: CreateScheduleUseCase,
  private val createOneTimeScheduleUseCase: CreateOneTimeScheduleUseCase,
  private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
  private val setScheduleFulfilledUseCase: SetScheduleFulfilledUseCase,
  private val getTaskUseCase: GetTaskUseCase,
  private val scheduleUserReplyUseCase: ScheduleUserReplyUseCase
) : Middleware<AppState> {

  val timeManager: TimeManager = TimeManagerImpl()

  override fun invoke(
    state: AppState,
    action: Action,
    dispatch: Dispatch,
    next: Next<AppState>,
    scope: CoroutineScope,
  ): Action {
    when (action) {

      is UserAction.Authenticated -> scope.launch {
        scheduleWorkUseCase() // TODO: test that the sync is triggered
      }

      is TaskAction.TaskCreatedAction -> scope.launch {
        with(action) {
          createOneTimeScheduleUseCase(task.id, priority).map {
            dispatch(ScheduleCreatedAction(it, task))
          }
        }
      }

      is TaskAction.CompleteTasksAction -> scope.launch {
        if (action.complete) {
          dispatch(CancelScheduleAction(action.taskIds))
        }
      }

      is RescheduleTaskAction -> scope.launch {
        with(action) {
          getTaskUseCase(taskId).map { task ->
            createScheduleUseCase(taskId, scheduleType, localDateTime).map { schedule ->
              dispatch(ScheduleCreatedAction(schedule, task))
            }
          }
        }
      }

      is CancelActiveScheduleAction -> scope.launch {
        cancelActiveScheduleUseCase(listOf(action.taskId), listOf(ScheduleType.OneTime))
      }

      is ScheduleAction.ScheduleReplyAction -> {
        logger.i { "⏳ ScheduleReplyAction: scheduleId=${action.scheduleId}, replyType=${action.replyType}" }

        scope.launch {
          val result = scheduleUserReplyUseCase(action.scheduleId, action.replyType)

          result.onRight { actionResult ->
            if (actionResult != null) {
              logger.i { "🚀 Ejecutando acción resultante del use case: $actionResult" }
              dispatch(actionResult)
            } else {
              logger.i { "⚠️ No se generó ninguna acción (null)" }
            }
          }

          result.onLeft { failure ->
            logger.e { "❌ Error al ejecutar ScheduleUserReplyUseCase: $failure" }
          }
        }
      }


      is ScheduleCreatedAction -> {
        scope.launch {
          action.schedule.scheduledAt.let { time ->
            scheduleAtTimeUseCase(action.schedule.id, time, action.task.title, action.task.id)
          }
        }
      }

      is ExecuteScheduleAction -> scope.launch {
        getScheduleUseCase(action.scheduleId).map { schedule ->
          if (schedule.active) {
            setScheduleFulfilledUseCase(schedule.id)
            dispatch(MessageAction.CreateScheduleMessageAction(schedule.id))
          }
        }
      }

      is ToggleReminderScheduleAction -> scope.launch {
        /* toggleQuickReminderUseCase(action.taskId).map { //TODO: fix this later
             dispatch(ScheduleCreatedAction(it))
         }*/
      }

      is CreateReminderScheduleAction -> scope.launch {
        /* createReminderUseCase(action.taskId).map { //TODO: fix this later
             dispatch(ScheduleCreatedAction(it))
         }*/
      }

      is CancelScheduleAction -> scope.launch {
        cancelActiveScheduleUseCase(action.taskIds, action.scheduleType)
      }

      else -> NoOp
    }

    return next(state, action, dispatch)
  }

  // TODO: create use-case for this action - this would mean creating a
  private fun scheduleUserReply(
    scope: CoroutineScope,
    scheduleId: Uuid,
    replyType: ReplyType,
    dispatch: Dispatch
  ) {
    scope.launch {
      getScheduleUseCase(scheduleId).onRight { schedule ->
        when (replyType) {
          ReplyType.LATER -> {}
          ReplyType.TOMORROW -> {
            val tomorrowTime = timeManager.tomorrowLocalDateTime(12)
            dispatch(
              RescheduleTaskAction(
                schedule.taskId,
                schedule.scheduleType,
                tomorrowTime
              )
            )
          }

          ReplyType.SNOOZE -> {
            val snoozeTime =
              timeManager.todayLocalDateTimeByAdding(hour = 1, minute = 0)
            dispatch(
              RescheduleTaskAction(
                schedule.taskId,
                schedule.scheduleType,
                snoozeTime
              )
            )
          }

          ReplyType.DONE -> {
            dispatch(
              TaskAction.CompleteTasksAction(listOf(schedule.taskId), true)
            )
          }
        }
      }
    }
  }
}