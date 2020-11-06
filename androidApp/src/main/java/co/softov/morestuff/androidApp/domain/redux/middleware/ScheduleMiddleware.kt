package co.softov.morestuff.androidApp.domain.redux.middleware

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.enums.Priority.*
import co.softov.morestuff.androidApp.domain.enums.ReplyType.*
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.redux.Action
import co.softov.morestuff.androidApp.domain.redux.AppState
import co.softov.morestuff.androidApp.domain.redux.NoOp
import co.softov.morestuff.androidApp.domain.redux.middleware.MessageAction.CreateScheduleMessageAction
import co.softov.morestuff.androidApp.domain.redux.middleware.NotificationAction.RemoveScheduleNotificationAction
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.ScheduleReplyAction
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction.*
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCompleteAction
import co.softov.morestuff.androidApp.domain.redux.middleware.TaskAction.TaskCreatedAction
import co.softov.morestuff.androidApp.domain.service.Scheduler
import co.softov.morestuff.androidApp.domain.usecase.schedule.CancelActiveScheduleUseCase
import co.softov.morestuff.androidApp.domain.usecase.schedule.CreateScheduleUseCase
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetScheduleUseCase
import co.softov.morestuff.androidApp.domain.usecase.schedule.SetScheduleFulfilledUseCase
import com.iiitech.operations.domain.redux.Dispatch
import com.iiitech.operations.domain.redux.Middleware
import com.iiitech.operations.domain.redux.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class ScheduleAction : Action.FeatureAction() {
    data class ExecuteScheduleAction(val scheduleId: Long) : ScheduleAction()
    data class RescheduleAction(val taskId: Long, val priority: Priority) : ScheduleAction()

    internal data class ScheduleCreatedAction(val schedule: Schedule) : ScheduleAction()
}

class ScheduleMiddleware(
    private val scheduler: Scheduler,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val setScheduleFulfilledUseCase: SetScheduleFulfilledUseCase
) : Middleware<AppState> {

    override fun invoke(
        state: AppState,
        action: Action,
        dispatch: Dispatch,
        next: Next<AppState>,
        scope: CoroutineScope
    ): Action {
        when (action) {

            is TaskCreatedAction -> scope.launch {
                createScheduleUseCase(action.task.id, action.priority).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is TaskCompleteAction -> scope.launch {
                cancelActiveSchedule(action.taskId, dispatch)
            }

            is RescheduleAction -> scope.launch {
                cancelActiveSchedule(action.taskId, dispatch)
                createScheduleUseCase(action.taskId, action.priority).map {
                    dispatch(ScheduleCreatedAction(it))
                }
            }

            is ScheduleReplyAction -> scope.launch {
                when (action.replyType) {
                    LATER -> dispatch(RescheduleAction(action.schedule.taskId, Later()))
                    SNOOZE -> dispatch(RescheduleAction(action.schedule.taskId, Today()))
                    TOMORROW -> dispatch(RescheduleAction(action.schedule.taskId, Tomorrow()))
                    DONE -> dispatch(TaskCompleteAction(action.schedule.taskId))
                }
            }

            is ScheduleCreatedAction -> {
                action.schedule.apply {
                    scheduleTime?.let { time -> scheduler.scheduleAtExact(id, time) }
                }
            }

            is ExecuteScheduleAction -> scope.launch {
                getScheduleUseCase(action.scheduleId).map { schedule ->
                    if (schedule.active) {
                        setScheduleFulfilledUseCase(schedule.id)
                        dispatch(CreateScheduleMessageAction(schedule.id))
                    }
                }
            }

            else -> NoOp
        }

        return next(state, action, dispatch)
    }

    private suspend fun cancelActiveSchedule(taskId: Long, dispatch: Dispatch) {
        cancelActiveScheduleUseCase(taskId).map { schedule ->
            dispatch(RemoveScheduleNotificationAction(schedule.id))
        }
    }
}